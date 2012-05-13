package hash.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class Asm implements Opcodes {

	public static ClassWriter newClassWriter() {
		return new ClassWriter(ClassWriter.COMPUTE_MAXS
				| ClassWriter.COMPUTE_FRAMES);
	}

	public static void addDefaultConstructor(ClassWriter cw, Class<?> superclass) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null,
				null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, Asm.internalName(superclass),
				"<init>", "()V");
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	public static void addConstructor(ClassWriter cw, Class<?> superclass,
			Class<?>... argTypes) {
		String descriptor = null;
		try {
			descriptor = Type.getConstructorDescriptor(superclass
					.getConstructor(argTypes));

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", descriptor,
				null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		int i = 1;
		for (Class<?> argType : argTypes) {
			load(mv, argType, i);
			i++;
		}
		mv.visitMethodInsn(INVOKESPECIAL, Asm.internalName(superclass),
				"<init>", descriptor);
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	public static void load(MethodVisitor mv, Class<?> argType, int index) {
		if (argType == Boolean.TYPE || argType == Character.TYPE
				|| argType == Byte.TYPE || argType == Short.TYPE
				|| argType == Integer.TYPE)
			mv.visitVarInsn(ILOAD, index);
		else if (argType == Long.TYPE)
			mv.visitVarInsn(LLOAD, index);
		else if (argType == Float.TYPE)
			mv.visitVarInsn(FLOAD, index);
		else if (argType == Double.TYPE)
			mv.visitVarInsn(DLOAD, index);
		else
			mv.visitVarInsn(ALOAD, index);
	}

	public static String descriptor(Class<?> klass) {
		return Type.getDescriptor(klass);
	}

	public static String[] descriptors(Class<?>... klasses) {
		String[] rv = new String[klasses.length];
		for (int i = 0; i < rv.length; i++)
			rv[i] = Type.getDescriptor(klasses[i]);
		return rv;
	}

	public static String internalName(Class<?> klass) {
		return Type.getInternalName(klass);
	}

	public static String[] internalNames(Class<?>... klasses) {

		String[] rv = new String[klasses.length];
		for (int i = 0; i < rv.length; i++)
			rv[i] = Type.getInternalName(klasses[i]);
		return rv;
	}

	public static String singleMethodName(Class<?> klass) {
		Method[] methods = klass.getDeclaredMethods();
		if (methods.length == 0)
			throw new RuntimeException(String.format(
					"Class '%s' has no methods", klass.getCanonicalName()));
		return methods[0].getName();
	}

	public static String singleMethodDescriptor(Class<?> klass) {
		Method[] methods = klass.getDeclaredMethods();
		if (methods.length == 0)
			throw new RuntimeException(String.format(
					"Class '%s' has no methods", klass.getCanonicalName()));
		Method method = methods[0];
		return Type.getMethodDescriptor(method);
	}

	public static String methodDescriptor(Class<?> klass, String name) {
		Method[] methods = klass.getDeclaredMethods();
		Method method = null;
		for (Method m : methods)
			if (m.getName().equals(name)) {
				method = m;
				break;
			}
		if (method == null)
			throw new RuntimeException(String.format(
					"Class '%s' has no method named '%s'",
					klass.getCanonicalName(), name));
		return Type.getMethodDescriptor(method);
	}

	public static void invokeStatic(MethodVisitor mv, Class<?> klass,
			String name) {
		mv.visitMethodInsn(INVOKESTATIC, internalName(klass), name,
				methodDescriptor(klass, name));
	}

	public static void invokeStatic(MethodVisitor mv, Method method) {
		mv.visitMethodInsn(INVOKESTATIC,
				internalName(method.getDeclaringClass()), method.getName(),
				Type.getMethodDescriptor(method));
	}

	public static void invokeVirtual(MethodVisitor mv, Class<?> klass,
			String name) {
		mv.visitMethodInsn(INVOKEVIRTUAL, internalName(klass), name,
				methodDescriptor(klass, name));
	}

	public static void invokeVirtual(MethodVisitor mv, Method method) {
		mv.visitMethodInsn(INVOKEVIRTUAL,
				internalName(method.getDeclaringClass()), method.getName(),
				Type.getMethodDescriptor(method));
	}

	public static void constructAndInitialize(MethodVisitor mv, Class<?> klass) {
		mv.visitTypeInsn(NEW, internalName(klass));
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, internalName(klass), "<init>", "()V");
	}

	public static Class<?> getBoxedClass(Class<?> primitiveClass) {
		Class<?> rv = null;
		if (primitiveClass == Boolean.TYPE)
			rv = Boolean.class;
		else if (primitiveClass == Character.TYPE)
			rv = Character.class;
		else if (primitiveClass == Byte.TYPE)
			rv = Byte.class;
		else if (primitiveClass == Short.TYPE)
			rv = Short.class;
		else if (primitiveClass == Integer.TYPE)
			rv = Integer.class;
		else if (primitiveClass == Long.TYPE)
			rv = Long.class;
		else if (primitiveClass == Float.TYPE)
			rv = Float.class;
		else if (primitiveClass == Double.TYPE)
			rv = Double.class;
		return rv;
	}

	public static void unbox(MethodVisitor mv, Class<?> classForPrimitive) {
		String methodName = null;
		if (classForPrimitive == Boolean.class)
			methodName = "booleanValue";
		else if (classForPrimitive == Character.class)
			methodName = "charValue";
		else if (classForPrimitive == Byte.class)
			methodName = "byteValue";
		else if (classForPrimitive == Short.class)
			methodName = "shortValue";
		else if (classForPrimitive == Integer.class)
			methodName = "intValue";
		else if (classForPrimitive == Long.class)
			methodName = "longValue";
		else if (classForPrimitive == Float.class)
			methodName = "floatValue";
		else if (classForPrimitive == Double.class)
			methodName = "doubleValue";
		try {
			invokeVirtual(mv, classForPrimitive.getMethod(methodName));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void invokeInit(MethodVisitor mv, Constructor constructor) {
		String descriptor = Type.getConstructorDescriptor(constructor);
		mv.visitMethodInsn(INVOKESPECIAL,
				Asm.internalName(constructor.getDeclaringClass()), "<init>",
				descriptor);
	}

}
