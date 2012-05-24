package hash.parsing.visitors;

import hash.parsing.tree.EmptyNode;

/**
 * Node for storing some result relevant to a particular kind of tree walking
 * 
 * @author Thiago de Arruda
 * 
 */
public class Result extends EmptyNode  {
	private Object evaluationResult;

	public Result(Object evaluationResult) {
		this.evaluationResult = evaluationResult;
	}

	public Object getNodeData() {
		return evaluationResult;
	}
}