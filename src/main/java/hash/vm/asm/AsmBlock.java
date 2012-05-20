package hash.vm.asm;

import hash.vm.Block;
import hash.vm.Statement;

import org.objectweb.asm.MethodVisitor;

public class AsmBlock extends Block implements AsmStatement {

	public void generate(MethodVisitor mv) {
		for (Statement statement : getStatements())
			((AsmStatement) statement).generate(mv);
	}

}