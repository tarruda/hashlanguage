package hash.vm;

import java.util.ArrayList;
import java.util.List;

public class If extends Statement {
	private List<BranchCondition> conditions = new ArrayList<BranchCondition>();
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

	public void addCondition(BranchCondition condition) {
		conditions.add(condition);
	}

	public Iterable<BranchCondition> getConditions() {
		return conditions;
	}
}
