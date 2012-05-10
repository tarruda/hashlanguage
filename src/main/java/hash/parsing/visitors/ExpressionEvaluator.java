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

	@Override
	protected Tree visitUnaryExpression(Tree operator, Tree operand) {
		String operatorTxt = operator.getText();
		if (operatorTxt.equals("+"))// ignore
			return visit(operand);
		Result op = (Result) visit(operand);
		return new Result(Lookup.invokeUnaryOperator(operatorTxt,
				op.getEvaluationResult()));
	}
}
