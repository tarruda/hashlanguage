package hash.vm;


public class If extends ConditionalBranch {
	private Statement trueStatement;
	private Statement falseStatement;

	public Statement getTrueStatement() {
		return trueStatement;
	}

	public void setTrueStatement(Statement trueStatement) {
		this.trueStatement = trueStatement;
	}

	public Statement getFalseStatement() {
		return falseStatement;
	}

	public void setFalseStatement(Statement falseStatement) {
		this.falseStatement = falseStatement;
	}
}
