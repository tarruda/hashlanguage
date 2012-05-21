package hash.runtime;

import hash.runtime.functions.JavaMethod;
import hash.runtime.mixins.ArrayMixin;
import hash.runtime.mixins.BooleanMixin;
import hash.runtime.mixins.CharacterMixin;
import hash.runtime.mixins.FloatMixin;
import hash.runtime.mixins.IntegerMixin;
import hash.runtime.mixins.ListMixin;
import hash.runtime.mixins.MapMixin;
import hash.runtime.mixins.NumberMixin;
import hash.runtime.mixins.ObjectMixin;
import hash.runtime.mixins.RegexMixin;
import hash.runtime.mixins.StringMixin;
import hash.util.Constants;
import hash.util.Err;
import hash.vm.ClassGenerator;
import hash.vm.ConstructorInvocation;
import hash.vm.If;
import hash.vm.InstanceMethodInvocation;
import hash.vm.Invocation;
import hash.vm.MethodGenerator;
import hash.vm.StaticMethodInvocation;
import hash.vm.VirtualMachineCodeFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
	private final HashMap<Class<?>, Map[]> classMixins;
	private final VirtualMachineCodeFactory f = VirtualMachineCodeFactory.Instance;

	private JvmBridge() {
		classMap = new HashMap<Class<?>, HashObject>();
		classMixins = new HashMap<Class<?>, Map[]>();
		classMixins.put(Object.class, new Map[] { ObjectMixin.INSTANCE });
		classMixins.put(Boolean.class, new Map[] { BooleanMixin.INSTANCE });
		classMixins.put(Number.class, new Map[] { NumberMixin.INSTANCE });
		classMixins.put(Character.class, new Map[] { IntegerMixin.INSTANCE,
				CharacterMixin.INSTANCE });
		classMixins.put(Byte.class, new Map[] { IntegerMixin.INSTANCE });
		classMixins.put(Short.class, new Map[] { IntegerMixin.INSTANCE });
		classMixins.put(Integer.class, new Map[] { IntegerMixin.INSTANCE });
		classMixins.put(Long.class, new Map[] { IntegerMixin.INSTANCE });
		classMixins.put(Float.class, new Map[] { FloatMixin.INSTANCE });
		classMixins.put(Double.class, new Map[] { FloatMixin.INSTANCE });
		classMixins.put(String.class, new Map[] { StringMixin.INSTANCE });
		classMixins.put(List.class, new Map[] { ListMixin.INSTANCE });
		classMixins.put(Map.class, new Map[] { MapMixin.INSTANCE });
		classMixins.put(Pattern.class, new Map[] { RegexMixin.INSTANCE });
		HashObject klass = getAdapterFor(HashObject.class);
		klass.remove("setIsa");
		klass.remove("getIsa");
		klass.remove(Constants.CONSTRUCTOR);
	}

	public HashObject getAdapterFor(Class<?> cls) {
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

		// If mixins have been defined for this class or any of its interfaces,
		// the time to merge is now. Begin by merging the interfaces mixins, so
		// classes mixins can override them
		Class<?>[] interfaces = klass.getInterfaces();
		for (Class<?> i : interfaces) {
			Map[] interfaceMixins = classMixins.get(i);
			if (interfaceMixins != null)
				for (Map mixin : interfaceMixins)
					for (Object key : mixin.keySet())
						hashClass.put(key, mixin.get(key));
		}
		Map[] mixins = classMixins.get(klass);
		if (mixins != null)
			for (Map mixin : mixins)
				for (Object key : mixin.keySet())
					hashClass.put(key, mixin.get(key));
		// If this is an array class, explicitly merge the array mixin
		if (klass.isArray())
			for (Object key : ArrayMixin.INSTANCE.keySet())
				hashClass.put(key, ArrayMixin.INSTANCE.get(key));

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
		gen.addConstructor(String.class, String.class, Boolean.TYPE);
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
				rv.add(new MethodOrConstructor(method));
			}
			return rv;
		}

		public static List<MethodOrConstructor> getDeclaredConstructors(
				Class<?> klass) {
			ArrayList<MethodOrConstructor> rv = new ArrayList<MethodOrConstructor>();
			for (Constructor constructor : klass.getDeclaredConstructors()) {
				int mod = constructor.getModifiers();
				if (Modifier.isPublic(mod))
					rv.add(new MethodOrConstructor(constructor));
			}
			return rv;
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
