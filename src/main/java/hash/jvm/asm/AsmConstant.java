package hash.jvm.asm;

import hash.jvm.Constant;

import org.objectweb.asm.MethodVisitor;

public class AsmConstant extends Constant implements AsmExpression {

	public void generate(MethodVisitor mv) {
		mv.visitLdcInsn(getValue());
	}
}