package org.hashlang.jvm.asm;


import org.hashlang.jvm.Block;
import org.hashlang.jvm.Statement;
import org.objectweb.asm.MethodVisitor;

public class AsmBlock extends Block implements AsmStatement {

	public void generate(MethodVisitor mv) {
		for (Statement statement : getStatements())
			((AsmStatement) statement).generate(mv);
	}

}