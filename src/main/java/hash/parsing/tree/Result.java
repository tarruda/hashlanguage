package hash.parsing.tree;

/**
 * Node for storing the result of program evaluation
 * 
 * @author Thiago de Arruda
 * 
 */
public class Result extends EmptyNode implements HashNode {
	private Object evaluationResult;

	public Result(Object evaluationResult) {
		this.evaluationResult = evaluationResult;
	}

	public Object getNodeData(Object key) {
		throw new UnsupportedOperationException();
	}

	public void setNodeData(Object key, Object value) {
		throw new UnsupportedOperationException();
	}

	public void setNodeData(Object value) {
		throw new UnsupportedOperationException();
	}

	public Object getNodeData() {
		return evaluationResult;
	}

	public void setText(String text) {
		throw new UnsupportedOperationException();
	}

}