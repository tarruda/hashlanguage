package hash.parsing.visitors.evaluators;

import hash.parsing.tree.EmptyNode;

public class ReturnStatement extends EmptyNode {

	private Object returnedValue;

	public ReturnStatement(Object returnedValue) {
		this.returnedValue = returnedValue;
	}

	public Object getNodeData() {
		return returnedValue;
	}
}
