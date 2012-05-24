package hash.jvm;

import java.util.ArrayList;
import java.util.List;

public abstract class ConditionalBranch extends Statement {
	private List<BranchCondition> conditions = new ArrayList<BranchCondition>();

	public void addCondition(BranchCondition condition) {
		conditions.add(condition);
	}

	public Iterable<BranchCondition> getConditions() {
		return conditions;
	}
}
