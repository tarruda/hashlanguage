package hash.parsing.walkers.evaluators;

/**
 * Node for storing the result of evaluation
 * @author Thiago de Arruda
 *
 */
public class Result extends EmptyNode {
	private Object evaluationResult;

	public Result(Object evaluationResult) {
		this.evaluationResult = evaluationResult;
	}

	public Object getEvaluationResult() {
		return evaluationResult;
	}
}