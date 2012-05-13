package hash.runtime;

import hash.lang.Function;
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
import hash.runtime.mixins.StringMixin;
import hash.util.Asm;
import hash.util.Err;

import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class HashAdapter implements Opcodes {

	private static final HashMap<Class<?>, HashObject> classMap;
	private static final HashMap<Class<?>, Map[]> classMixins;

	private static final String[] ignoredMethodNames = { "getClass" };

	static {
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
	}

	public static HashObject getHashClass(Class<?> cls) {
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
		HashObject hashClass = Factory.createObject();
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
		if (klass == Object.class && name.equals("toString"))
			try {
				new FileOutputStream("/tmp/object.class").write(classData);
			} catch (Exception ex) {

			}
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
				Class<?> kls = params[i];
				if (kls.isPrimitive())
					kls = Asm.getBoxedClass(kls);
				mv.visitTypeInsn(INSTANCEOF, Asm.internalName(kls));
				mv.visitJumpInsn(IFEQ, nextTest);
			}
			// if the arguments match the signature, the method is invoked
			implementMethodInvocation(mv, method);
			// if not, the execution continues from this label
			mv.visitLabel(nextTest);
		}
		try {
			Asm.invokeStatic(mv, Err.class.getMethod("illegalJavaArgs"));
		} catch (Exception e) {
			throw Err.ex(e);
		}
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
