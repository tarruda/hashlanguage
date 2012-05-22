package hash.parsing.visitors.evaluators;

import hash.parsing.tree.EmptyNode;

public class ContinueStatement extends EmptyNode {
	public static ContinueStatement INSTANCE = new ContinueStatement();

	private ContinueStatement() {

	}
}
