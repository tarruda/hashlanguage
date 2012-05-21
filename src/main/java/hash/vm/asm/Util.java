package hash.vm.asm;

import hash.util.Err;
import hash.vm.AreEqual;
import hash.vm.BranchCondition;
import hash.vm.Expression;
import hash.vm.InstanceOf;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class Util implements Opcodes {

	public static String getMethodDescriptor(Class returnType,
			Class... parameterTypes) {
		Type[] argumentTypes = new Type[parameterTypes.length];
		for (int i = 0; i < argumentTypes.length; i++)
			argumentTypes[i] = Type.getType(parameterTypes[i]);
		return Type
				.getMethodDescriptor(Type.getType(returnType), argumentTypes);
	}

	public static String internalName(Class<?> c) {
		return Type.getInternalName(c);
	}

	public static String internalName(String name) {
		return name.replace('.', '/');
	}

	public static String descriptor(Class<?> klass) {
		return Type.getDescriptor(klass);
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

	public static void invokeStatic(MethodVisitor mv, Method method) {
		mv.visitMethodInsn(INVOKESTATIC,
				internalName(method.getDeclaringClass()), method.getName(),
				Type.getMethodDescriptor(method));
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

	public static void invokeInit(MethodVisitor mv, Constructor constructor) {
		String descriptor = Type.getConstructorDescriptor(constructor);
		mv.visitMethodInsn(INVOKESPECIAL,
				Util.internalName(constructor.getDeclaringClass()), "<init>",
				descriptor);
	}

	public static void generateConditionalBranch(MethodVisitor mv,
			BranchCondition condition, Label branch) {
		if (condition instanceof AreEqual)
			generateAreEqualBranch(mv, (AreEqual) condition, branch);
		else if (condition instanceof InstanceOf)
			generateInstanceOfBranch(mv, (InstanceOf) condition, branch);
		else
			throw Err.illegalArg("condition");
	}

	public static void generateInstanceOfBranch(MethodVisitor mv,
			InstanceOf condition, Label falseLabel) {
		((AsmExpression) condition.getObject()).generate(mv);
		mv.visitTypeInsn(INSTANCEOF,
				Util.internalName(condition.getBoxedClass()));
		mv.visitJumpInsn(IFEQ, falseLabel);
	}

	public static void generateAreEqualBranch(MethodVisitor mv,
			AreEqual condition, Label falseLabel) {
		Expression left = condition.getLeft();
		Expression right = condition.getRight();
		Class ldt = left.getDataType();
		Class rdt = right.getDataType();
		assert ldt == rdt;
		((AsmExpression) right).generate(mv);
		((AsmExpression) left).generate(mv);
		if (!ldt.isPrimitive())
			mv.visitJumpInsn(IF_ACMPNE, falseLabel);
		else if (ldt == Long.TYPE) {
			mv.visitInsn(LCMP);
			mv.visitJumpInsn(IFNE, falseLabel);
		} else if (ldt == Float.TYPE) {
			mv.visitInsn(FCMPG);
			mv.visitJumpInsn(IFNE, falseLabel);
		} else if (ldt == Double.TYPE) {
			mv.visitInsn(DCMPG);
			mv.visitJumpInsn(IFNE, falseLabel);
		} else
			mv.visitJumpInsn(IF_ICMPNE, falseLabel);
	}

	public static void cast(MethodVisitor mv, Class from, Class to) {
		if (to.isPrimitive())
			to = hash.vm.Util.getBoxedClass(to);
		if (!to.isAssignableFrom(from))
			mv.visitTypeInsn(CHECKCAST, internalName(to));
	}

	public static void box(MethodVisitor mv, Class c) {
		if (!c.isPrimitive())
			return;
		Class boxedClass = hash.vm.Util.getBoxedClass(c);
		try {
			invokeStatic(mv, boxedClass.getMethod("valueOf", c));
		} catch (Exception ex) {
			throw Err.ex(ex);
		}
	}

	public static void unbox(MethodVisitor mv, Class<?> c) {
		String methodName = null;
		if (c == Boolean.TYPE || c == Boolean.class) {
			methodName = "booleanValue";
			c = Boolean.class;
		} else if (c == Character.class || c == Character.TYPE) {
			methodName = "charValue";
			c = Character.class;
		} else if (c == Byte.class || c == Byte.TYPE) {
			methodName = "byteValue";
			c = Byte.class;
		} else if (c == Short.class || c == Short.TYPE) {
			methodName = "shortValue";
			c = Short.class;
		} else if (c == Integer.class || c == Integer.TYPE) {
			methodName = "intValue";
			c = Integer.class;
		} else if (c == Long.class || c == Long.TYPE) {
			methodName = "longValue";
			c = Long.class;
		} else if (c == Float.class || c == Float.TYPE) {
			methodName = "floatValue";
			c = Float.class;
		} else if (c == Double.class || c == Double.TYPE) {
			methodName = "doubleValue";
			c = Double.class;
		}
		try {
			invokeVirtual(mv, c.getMethod(methodName));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
