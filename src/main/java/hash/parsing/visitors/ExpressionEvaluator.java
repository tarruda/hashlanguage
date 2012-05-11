package hash.parsing.visitors;

import static hash.parsing.HashParser.ATTRIBUTE;
import static hash.parsing.HashParser.ITEM;
import hash.lang.Factory;
import hash.parsing.visitors.nodes.Result;
import hash.runtime.Lookup;

import java.util.List;
import java.util.Map;

import org.antlr.runtime.tree.Tree;

public class ExpressionEvaluator extends LiteralEvaluator {

	private Map context;

	public ExpressionEvaluator(Map context) {
		this.context = context;
	}

	@Override
	protected Tree visitAssignment(Tree node, Tree target, Tree expression) {
		Object value = ((Result) visit(expression)).getEvaluationResult();
		if (target.getType() == ATTRIBUTE || target.getType() == ITEM) {
			Object ownerObject = ((Result) visit(target.getChild(0)))
					.getEvaluationResult();
			Object key = ((Result) visit(target.getChild(1)))
					.getEvaluationResult();
			if (target.getType() == ATTRIBUTE)
				Lookup.setAttribute(ownerObject, key, value);
			else
				Lookup.setItem(ownerObject, key, value);
		} else
			// target is an identifier
			context.put(target.getText(), value);
		return new Result(value);
	}

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
		Object[] args = ((List) ((Result) visit(arguments))
				.getEvaluationResult()).toArray();
		if (expression.getType() == ATTRIBUTE || expression.getType() == ITEM) {
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
	protected Tree visitObject(Tree node) {
		Map rv = Factory.createObject();
		int len = node.getChildCount();
		for (int i = 0; i < len; i++) {
			Object key = ((Result) visit(node.getChild(i)))
					.getEvaluationResult();
			Object value = ((Result) visit(node.getChild(i).getChild(0)))
					.getEvaluationResult();
			rv.put(key, value);
		}
		return new Result(rv);
	}

	@Override
	protected Tree visitList(Tree node) {
		int len = node.getChildCount();
		List rv = Factory.createList();
		for (int i = 0; i < len; i++)
			rv.add(((Result) visit(node.getChild(i))).getEvaluationResult());
		return new Result(rv);
	}

	@Override
	protected Tree visitAttributeAccess(Tree node, Tree target,
			Tree attributeKey) {
		Object tgt = ((Result) visit(target)).getEvaluationResult();
		Object key = ((Result) visit(attributeKey)).getEvaluationResult();
		return new Result(Lookup.getAttribute(tgt, key));
	}

	@Override
	protected Tree visitItemAccess(Tree node, Tree target, Tree itemKey) {
		Object tgt = ((Result) visit(target)).getEvaluationResult();
		Object key = ((Result) visit(itemKey)).getEvaluationResult();
		return new Result(Lookup.getItem(tgt, key));
	}

	@Override
	protected Tree visitIdentifier(Tree node) {
		return new Result(context.get(node.getText()));
	}
}
