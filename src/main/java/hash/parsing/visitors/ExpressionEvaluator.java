package hash.parsing.visitors;

import hash.parsing.visitors.nodes.Result;
import hash.runtime.Lookup;

import org.antlr.runtime.tree.Tree;

public class ExpressionEvaluator extends LiteralEvaluator {

	@Override
	protected Tree visitBinaryExpression(Tree operator, Tree left, Tree right) {
		Result l = (Result) visit(left);
		Result r = (Result) visit(right);
		return new Result(Lookup.invokeBinaryOperator(operator.getText(),
				l.getEvaluationResult(), r.getEvaluationResult()));
	}

}
