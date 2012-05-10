package hash.parsing.visitors;

import static hash.parsing.HashParser.ATTRIBUTE;
import hash.parsing.visitors.nodes.Result;
import hash.runtime.Lookup;

import org.antlr.runtime.tree.Tree;

public class ExpressionEvaluator extends LiteralEvaluator {

	@Override
	protected Tree visitBinaryExpression(Tree operator, Tree left, Tree right) {
		Object l = ((Result) visit(left)).getEvaluationResult();
		Object r = ((Result) visit(right)).getEvaluationResult();
		return new Result(Lookup.invokeBinaryOperator(operator.getText(), l, r));
	}

	@Override
	protected Tree visitUnaryExpression(Tree operator, Tree operand) {
		String operatorTxt = operator.getText();
		if (operatorTxt.equals("+"))// ignore
			return visit(operand);
		Object op = ((Result) visit(operand)).getEvaluationResult();
		return new Result(Lookup.invokeUnaryOperator(operatorTxt, op));
	}

	@Override
	protected Tree visitInvocation(Tree node, Tree expression, Tree arguments) {
		Object[] args = (Object[]) ((Result) visit(arguments))
				.getEvaluationResult();
		if (expression.getType() == ATTRIBUTE) {
			// this is a method call
			Object tgt = ((Result) visit(expression.getChild(0)))
					.getEvaluationResult();
			Object methodKey = ((Result) visit(expression.getChild(1)))
					.getEvaluationResult();
			return new Result(Lookup.invokeMethod(tgt, methodKey, args));
		} else {
			// normal function call
			Object exp = ((Result) visit(arguments)).getEvaluationResult();
			return new Result(Lookup.invokeFunction(exp, args));
		}
	}

	@Override
	protected Tree visitArgs(Tree node) {
		int len = node.getChildCount();
		Object[] rv = new Object[len];
		for (int i = 0; i < len; i++)
			rv[i] = ((Result) visit(node.getChild(i))).getEvaluationResult();
		return new Result(rv);
	}

	@Override
	protected Tree visitAttributeAccess(Tree node, Tree target,
			Tree attributeKey) {
		Object tgt = ((Result) visit(target.getChild(0))).getEvaluationResult();
		Object key = ((Result) visit(target.getChild(1))).getEvaluationResult();
		return new Result(Lookup.getAttribute(tgt, key));
	}
}
