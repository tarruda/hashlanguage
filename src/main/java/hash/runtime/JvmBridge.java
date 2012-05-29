package hash.runtime;

import hash.jvm.ClassGenerator;
import hash.jvm.ConstructorInvocation;
import hash.jvm.If;
import hash.jvm.InstanceMethodInvocation;
import hash.jvm.Invocation;
import hash.jvm.MethodGenerator;
import hash.jvm.StaticMethodInvocation;
import hash.jvm.VirtualMachineCodeFactory;
import hash.runtime.functions.JavaMethod;
import hash.util.Constants;
import hash.util.Err;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.objectweb.asm.Opcodes;

/**
 * Class responsible for generating 'adapter' objects which allows hash code to
 * call java code
 * 
 * @author Thiago de Arruda
 * 
 */
public class JvmBridge extends ClassLoader implements Opcodes {
	public static final JvmBridge INSTANCE = new JvmBridge();

	private final HashMap<Class<?>, HashObject> classMap;

	private final VirtualMachineCodeFactory f = VirtualMachineCodeFactory.Instance;

	private JvmBridge() {
		classMap = new HashMap<Class<?>, HashObject>();
		HashObject klass = getWrapperFor(HashObject.class);
		klass.remove("setIsa");
		klass.remove("getIsa");
		klass.remove(Constants.CONSTRUCTOR);
	}

	public HashObject getWrapperFor(Class<?> cls) {
		if (!classMap.containsKey(cls))
			synchronized (classMap) {
				if (!classMap.containsKey(cls))
					constructHashClass(cls);
			}
		return classMap.get(cls);
	}

	private void constructHashClass(Class<?> klass) {
		Class<?> superclass = klass.getSuperclass();
		if (superclass != null && !classMap.containsKey(superclass))
			constructHashClass(superclass);
		HashObject hashClass = Factory.createObject();
		// group methods by name
		HashMap<String, List<MethodOrConstructor>> methodsByName = new HashMap<String, List<MethodOrConstructor>>();
		for (MethodOrConstructor method : MethodOrConstructor
				.getDeclaredMethods(klass)) {
			String name = method.getName();
			if (!methodsByName.containsKey(name))
				methodsByName.put(name, new ArrayList<MethodOrConstructor>());
			methodsByName.get(name).add(method);
		}
		// for each method group, create a wrapper function class
		// that will be responsible for delegating calls to the correct
		// method based on the parameters received
		for (String methodName : methodsByName.keySet()) {
			Class<?> wrapperClass = createJavaMethodAdapter(klass, methodName,
					methodsByName.get(methodName));
			try {
				Object instance = wrapperClass.getConstructor(String.class,
						String.class, Boolean.TYPE).newInstance(methodName,
						klass.getCanonicalName(),
						methodsByName.get(methodName).get(0).isStatic());
				hashClass.put(methodName, instance);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		// do the same for all constructors
		List<MethodOrConstructor> constructors = MethodOrConstructor
				.getDeclaredConstructors(klass);
		if (constructors.size() > 0) {
			Class<?> constructorWrapperClass = createJavaMethodAdapter(klass,
					"Constructors", constructors);
			try {
				Object instance = constructorWrapperClass.getConstructor(
						String.class, String.class, Boolean.TYPE).newInstance(
						"Constructors", klass.getCanonicalName(), true);
				hashClass.put(Constants.CONSTRUCTOR, instance);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		// if there is a superclass, then it must have already been loaded
		if (superclass != null)
			hashClass.setIsa(classMap.get(superclass));
		classMap.put(klass, hashClass);
	}

	private Class<?> createJavaMethodAdapter(Class<?> klass, String methodName,
			List<MethodOrConstructor> methods) {
		String fullname = "hash.generated." + klass.getCanonicalName() + "."
				+ methodName + "Wrapper";
		ClassGenerator gen = f.classGenerator(fullname, JavaMethod.class);
		gen.addSimpleConstructor(String.class, String.class, Boolean.TYPE);
		MethodGenerator m = gen.addMethod("invoke", Object.class,
				Object[].class);
		implementAdapter(m, methods);
		byte[] classData = gen.generate();
		return defineClass(fullname, classData, 0, classData.length);
	}

	private void implementAdapter(MethodGenerator methodGenerator,
			List<MethodOrConstructor> methods) {
		methodGenerator.addStatement(f.ifStmt());
		If currentIf = null;
		for (MethodOrConstructor method : methods) {
			if (currentIf == null)
				currentIf = (If) methodGenerator.get(0);
			else {
				currentIf.setFalseStatement(f.ifStmt());
				currentIf = (If) currentIf.getFalseStatement();
			}
			Class<?>[] params = method.getParameterTypes();
			// for each method overload we must test if the argument count
			// and types match with the actual method signature
			currentIf.addCondition(f.areEqual(f.argumentCount(),
					params.length + 1));
			for (int i = 0; i < params.length; i++)
				currentIf.addCondition(f.instanceOf(f.arg(i + 1), params[i]));
			// Invoke the method if the condition matches
			Invocation invocation = null;
			if (method.isConstructor()) {
				ConstructorInvocation inv = f.constructorInvocation();
				inv.setConstructor(method.getConstructor());
				invocation = inv;
			} else if (!method.isStatic()) {
				InstanceMethodInvocation inv = f.instanceMethodInvocation();
				inv.setTarget(f.arg(0));
				inv.setMethod(method.getMethod());
				invocation = inv;
			} else {
				StaticMethodInvocation inv = f.staticMethodInvocation();
				inv.setMethod(method.getMethod());
				invocation = inv;
			}
			for (int i = 1; i <= params.length; i++)
				invocation.addArgument(f.arg(i));
			currentIf.setTrueStatement(f.returnStmt(invocation));
		}
		try {
			methodGenerator.addStatement(f.throwStmt(f
					.staticMethodInvocation(Err.class
							.getMethod("illegalJavaArgs"))));
		} catch (Exception ex) {
			throw Err.ex(ex);
		}
	}

	private static class MethodOrConstructor {

		private static final String[] ignoredMethodNames = { "getClass" };

		public static List<MethodOrConstructor> getDeclaredMethods(
				Class<?> klass) {
			ArrayList<MethodOrConstructor> rv = new ArrayList<MethodOrConstructor>();
			for (Method method : klass.getDeclaredMethods()) {
				int mod = method.getModifiers();
				if (!Modifier.isPublic(mod) || Modifier.isAbstract(mod)
						|| Modifier.isPrivate(mod) || Modifier.isProtected(mod)
						|| isIgnored(method))
					continue;
				if (!Modifier.isPublic(klass.getModifiers()))
					// look for a public superclass/interface method that has
					// the same signature
					method = lookupAccessibleMethod(method, klass);
				if (method != null)
					rv.add(new MethodOrConstructor(method));
			}
			return rv;
		}

		public static List<MethodOrConstructor> getDeclaredConstructors(
				Class<?> klass) {
			ArrayList<MethodOrConstructor> rv = new ArrayList<MethodOrConstructor>();
			if (Modifier.isPublic(klass.getModifiers()))
				for (Constructor constructor : klass.getDeclaredConstructors()) {
					if (Modifier.isPublic(constructor.getModifiers()))
						rv.add(new MethodOrConstructor(constructor));
				}
			return rv;
		}

		private static Method lookupAccessibleMethod(Method method,
				Class<?> klass) {
			String name = method.getName();
			Class rtype = method.getReturnType();
			Class[] ptypes = method.getParameterTypes();
			for (Method m : klass.getDeclaredMethods())
				if (Modifier.isPublic(klass.getModifiers())
						&& Modifier.isPublic(m.getModifiers())
						&& m.getName().equals(name)
						&& m.getReturnType() == rtype
						&& Arrays.equals(ptypes, m.getParameterTypes()))
					return m;
			Method m = null;
			Class superClass = klass.getSuperclass();
			if (superClass != null)
				m = lookupAccessibleMethod(method, superClass);
			Class[] interfaces = klass.getInterfaces();
			for (int i = 0; m == null && i < interfaces.length; i++)
				m = lookupAccessibleMethod(method, interfaces[i]);
			return m;
		}

		private static boolean isIgnored(Method method) {
			for (String mName : ignoredMethodNames)
				if (mName.equals(method.getName()))
					return true;
			return false;
		}

		private Method method;
		private Constructor constructor;

		private MethodOrConstructor(Method method) {
			this.method = method;
		}

		private MethodOrConstructor(Constructor constructor) {
			this.constructor = constructor;
		}

		public Class<?>[] getParameterTypes() {
			if (method == null)
				return constructor.getParameterTypes();
			return method.getParameterTypes();
		}

		public String getName() {
			return method.getName();
		}

		public boolean isConstructor() {
			return method == null;
		}

		public boolean isStatic() {
			return method != null && Modifier.isStatic(method.getModifiers());
		}

		public Constructor getConstructor() {
			return constructor;
		}

		public Method getMethod() {
			return method;
		}

	}

}
