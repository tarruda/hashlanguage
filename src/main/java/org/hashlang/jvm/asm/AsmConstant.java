package org.hashlang.jvm.asm;


import org.hashlang.jvm.Constant;
import org.objectweb.asm.MethodVisitor;

public class AsmConstant extends Constant implements AsmExpression {

	public void generate(MethodVisitor mv) {
		mv.visitLdcInsn(getValue());
	}
}