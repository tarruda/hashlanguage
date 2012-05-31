package org.hashlang.jvm.asm;


import org.hashlang.jvm.ArgumentCount;
import org.objectweb.asm.MethodVisitor;

public class AsmArgumentCount extends ArgumentCount implements AsmExpression {

	public void generate(MethodVisitor mv) {	
		mv.visitVarInsn(ALOAD, 1);
		mv.visitInsn(ARRAYLENGTH);
	}

}