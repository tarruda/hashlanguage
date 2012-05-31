package org.hashlang.jvm.asm;


import org.hashlang.jvm.Local;
import org.objectweb.asm.MethodVisitor;

public class AsmLocal extends Local implements AsmExpression {

	public void generate(MethodVisitor mv) {
		int index = getIndex();
		Class argType = getType();
		int inst = ALOAD;
		if (argType == Boolean.TYPE || argType == Character.TYPE
				|| argType == Byte.TYPE || argType == Short.TYPE
				|| argType == Integer.TYPE)
			inst = ILOAD;
		else if (argType == Long.TYPE)
			inst = LLOAD;
		else if (argType == Float.TYPE)
			inst = FLOAD;
		else if (argType == Double.TYPE)
			inst = DLOAD;
		mv.visitVarInsn(inst, index);
	}
}
