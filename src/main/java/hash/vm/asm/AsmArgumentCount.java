package hash.vm.asm;

import hash.vm.ArgumentCount;

import org.objectweb.asm.MethodVisitor;

public class AsmArgumentCount extends ArgumentCount implements AsmExpression {

	public void generate(MethodVisitor mv) {	
		mv.visitVarInsn(ALOAD, 1);
		mv.visitInsn(ARRAYLENGTH);
	}

}