package hash.jvm.asm;

import hash.jvm.BranchCondition;
import hash.jvm.If;
import hash.jvm.Statement;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class AsmIf extends If implements AsmStatement {

	public void generate(MethodVisitor mv) {
		Label branch = new Label();
		for (BranchCondition condition : getConditions())
			Util.generateConditionalBranch(mv, condition, branch);
		Statement trueStmt = getTrueStatement();
		Statement falseStmt = getFalseStatement();
		((AsmStatement) trueStmt).generate(mv);
		mv.visitLabel(branch);
		if (falseStmt != null)
			((AsmStatement) falseStmt).generate(mv);
	}
}