package hash.vm.asm;

import hash.vm.Constant;

import org.objectweb.asm.MethodVisitor;

public class AsmConstant extends Constant implements AsmExpression {

	public void generate(MethodVisitor mv) {
		mv.visitLdcInsn(getValue());
	}
}