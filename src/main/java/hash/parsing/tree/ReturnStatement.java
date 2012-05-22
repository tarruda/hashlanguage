package hash.parsing.tree;

public class ReturnStatement extends EmptyNode {

	private Object returnedValue;

	public ReturnStatement(Object returnedValue) {
		this.returnedValue = returnedValue;
	}

	public Object getNodeData() {
		return returnedValue;
	}
}
