package hash.vm.asm;

import hash.vm.Arg;

import org.objectweb.asm.MethodVisitor;

public class AsmArg extends Arg implements AsmExpression {
	
	public void generate(MethodVisitor mv) {
		mv.visitVarInsn(ALOAD, 1);
		mv.visitLdcInsn(getIndex());
		mv.visitInsn(AALOAD);
	}

}