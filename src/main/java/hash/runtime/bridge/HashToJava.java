package hash.runtime.bridge;

import hash.lang.Function;
import hash.lang.Hash;
import hash.runtime.exceptions.IncompatibleJavaMethodSignatureException;
import hash.runtime.functions.JavaMethod;
import hash.runtime.mixins.IntegerMixin;
import hash.runtime.mixins.NumberMixin;
import hash.runtime.mixins.ObjectMixin;
import hash.runtime.mixins.StringMixin;
import hash.util.Asm;
import hash.util.Constants;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class HashToJava implements Opcodes {

	private static final HashMap<Class<?>, Hash> classMap;
	private static final HashMap<Class<?>, Hash> classMixins;
	private static final String[] ignoredMethodNames = { "getClass" };

	static {
		classMap = new HashMap<Class<?>, Hash>();
		classMixins = new HashMap<Class<?>, Hash>();
		classMixins.put(Object.class, new ObjectMixin());
		classMixins.put(Number.class, new NumberMixin());
		classMixins.put(Integer.class, new IntegerMixin());
		classMixins.put(Long.class, new IntegerMixin());
		classMixins.put(String.class, new StringMixin());
	}

	public static Hash getClass(Object object) {
		if (object instanceof Hash)
			return (Hash) object;
		return getSuperclass(object);
	}

	public static Hash getSuperclass(Object object) {
		if (object instanceof Hash) {
			Object rv = ((Hash) object).get(Constants.SUPER);
			if (rv instanceof Hash)
				return (Hash) rv;
			return null;
		}
		Class<?> cls = object.getClass();
		if (!classMap.containsKey(cls))
			synchronized (classMap) {
				if (!classMap.containsKey(cls))
					constructHashClass(cls);
			}
		return classMap.get(cls);
	}

	private static void constructHashClass(Class<?> klass) {
		Class<?> superclass = klass.getSuperclass();
		if (superclass != null && !classMap.containsKey(superclass))
			constructHashClass(superclass);
		Hash hashClass = new Hash();
		// group methods by name
		HashMap<String, List<Method>> methodsByName = new HashMap<String, List<Method>>();
		for (Method method : klass.getDeclaredMethods()) {
			int mod = method.getModifiers();
			if (Modifier.isAbstract(mod) || Modifier.isPrivate(mod)
					|| Modifier.isProtected(mod) || isIgnored(method))
				continue;
			String name = method.getName();
			if (!methodsByName.containsKey(name))
				methodsByName.put(name, new ArrayList<Method>());
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
						String.class, Boolean.TYPE).newInstance(
						methodName,
						klass.getCanonicalName(),
						Modifier.isStatic(methodsByName.get(methodName).get(0)
								.getModifiers()));
				hashClass.put(methodName, instance);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		// if we have defined a mixin for this class, the time to merge is now
		Hash mixin = classMixins.get(klass);
		if (mixin != null)
			for (Object key : mixin.keySet())
				hashClass.put(key, mixin.get(key));
		// if there is a superclass, then it must have already been loaded
		hashClass.put(Constants.SUPER, classMap.get(superclass));
		classMap.put(klass, hashClass);
	}

	private static boolean isIgnored(Method method) {
		for (String mName : ignoredMethodNames)
			if (mName.equals(method.getName()))
				return true;
		return false;
	}

	private static Class<?> createJavaMethodAdapter(Class<?> klass,
			String name, List<Method> methods) {
		String classNameSuffix = name + "Wrapper";
		ClassWriter cw = Asm.newClassWriter();
		cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER,
				"hash/generated/" + Asm.internalName(klass) + "/"
						+ classNameSuffix, null,
				Asm.internalName(JavaMethod.class), null);
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_VARARGS,
				Asm.singleMethodName(Function.class),
				Asm.singleMethodDescriptor(Function.class), null, null);
		mv.visitCode();
		implementAdapter(mv, methods);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		Asm.addConstructor(cw, JavaMethod.class, String.class, String.class,
				Boolean.TYPE);
		cw.visitEnd();
		byte[] classData = cw.toByteArray();
		return AdapterLoader.instance.defineClass(
				"hash.generated." + klass.getCanonicalName() + "."
						+ classNameSuffix, classData);
	}

	private static void implementAdapter(MethodVisitor mv, List<Method> methods) {
		for (Method method : methods) {
			// for each method overload we must test if the argument count
			// and types match with the actual method signature
			Label nextTest = new Label();
			Class<?>[] params = method.getParameterTypes();
			mv.visitVarInsn(ALOAD, 1);
			mv.visitInsn(ARRAYLENGTH);
			mv.visitLdcInsn(params.length + 1);
			mv.visitJumpInsn(IF_ICMPNE, nextTest);
			for (int i = 0; i < params.length; i++) {
				mv.visitVarInsn(ALOAD, 1);
				mv.visitLdcInsn(i + 1);
				mv.visitInsn(AALOAD);
				// try {
				//
				// Asm.invokeVirtual(mv, Object.class.getMethod("getClass"));
				// } catch (Exception e) {
				// throw new RuntimeException(e);
				// }
				Class<?> kls = params[i];
				if (kls.isPrimitive())
					kls = Asm.getBoxedClass(kls);
				mv.visitTypeInsn(INSTANCEOF, Asm.internalName(kls));
				// mv.visitLdcInsn(Type.getType(params[i]));
				mv.visitJumpInsn(IFEQ, nextTest);
			}
			// if the arguments match the signature, the method is invoked
			implementMethodInvocation(mv, method);
			// if not, the execution continues from this label
			mv.visitLabel(nextTest);
		}
		Asm.constructAndInitialize(mv,
				IncompatibleJavaMethodSignatureException.class);
		mv.visitInsn(ATHROW);
	}

	private static void implementMethodInvocation(MethodVisitor mv,
			Method method) {
		if (!Modifier.isStatic(method.getModifiers())) {
			// load and cast the instance since we are dealing with normal
			// methods
			mv.visitVarInsn(ALOAD, 1);
			mv.visitLdcInsn(0);
			mv.visitInsn(AALOAD);
			mv.visitTypeInsn(CHECKCAST,
					Asm.internalName(method.getDeclaringClass()));
		}
		// load and cast the remaining arguments
		int i = 1;
		for (Class<?> param : method.getParameterTypes()) {
			mv.visitVarInsn(ALOAD, 1);
			mv.visitLdcInsn(i++);
			mv.visitInsn(AALOAD);
			if (param.isPrimitive()) {
				Class<?> boxedClass = Asm.getBoxedClass(param);
				mv.visitTypeInsn(CHECKCAST, Asm.internalName(boxedClass));
				Asm.unbox(mv, boxedClass);
			} else
				mv.visitTypeInsn(CHECKCAST, Asm.internalName(param));
		}
		// invoke the actual method
		if (!Modifier.isStatic(method.getModifiers()))
			Asm.invokeVirtual(mv, method);
		else
			Asm.invokeStatic(mv, method);
		Class<?> returnClass = method.getReturnType();
		if (returnClass.isPrimitive()) {
			Class<?> wrapperClass = null;
			wrapperClass = Asm.getBoxedClass(returnClass);
			if (wrapperClass == null)
				mv.visitInsn(ACONST_NULL);
			else
				try {
					Asm.invokeStatic(mv,
							wrapperClass.getMethod("valueOf", returnClass));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
		}
		mv.visitInsn(ARETURN);
	}

}
