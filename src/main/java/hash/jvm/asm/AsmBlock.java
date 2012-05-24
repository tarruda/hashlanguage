package hash.jvm.asm;

import hash.jvm.Block;
import hash.jvm.Statement;

import org.objectweb.asm.MethodVisitor;

public class AsmBlock extends Block implements AsmStatement {

	public void generate(MethodVisitor mv) {
		for (Statement statement : getStatements())
			((AsmStatement) statement).generate(mv);
	}

}