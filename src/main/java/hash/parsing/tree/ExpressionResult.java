package hash.parsing.tree;

/**
 * Node for storing the result of program evaluation
 * 
 * @author Thiago de Arruda
 * 
 */
public class ExpressionResult extends EmptyNode  {
	private Object evaluationResult;

	public ExpressionResult(Object evaluationResult) {
		this.evaluationResult = evaluationResult;
	}

	public Object getNodeData() {
		return evaluationResult;
	}
}