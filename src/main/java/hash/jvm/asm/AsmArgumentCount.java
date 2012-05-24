package hash.jvm.asm;

import hash.jvm.ArgumentCount;

import org.objectweb.asm.MethodVisitor;

public class AsmArgumentCount extends ArgumentCount implements AsmExpression {

	public void generate(MethodVisitor mv) {	
		mv.visitVarInsn(ALOAD, 1);
		mv.visitInsn(ARRAYLENGTH);
	}

}