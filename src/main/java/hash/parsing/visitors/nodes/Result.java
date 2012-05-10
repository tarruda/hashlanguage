package hash.parsing.visitors.nodes;

import org.antlr.runtime.tree.CommonTree;

public class Result extends CommonTree {
	private Object evaluationResult;

	public Result(Object evaluationResult) {
		this.evaluationResult = evaluationResult;
	}

	public Object getEvaluationResult() {
		return evaluationResult;
	}
}