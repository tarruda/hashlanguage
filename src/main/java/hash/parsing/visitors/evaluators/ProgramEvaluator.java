package hash.parsing.visitors.evaluators;

import static hash.parsing.HashParser.ATTRIBUTE;
import static hash.parsing.HashParser.INDEX;
import hash.lang.Scope;
import hash.parsing.visitors.nodes.Result;
import hash.runtime.Factory;
import hash.runtime.Runtime;

import java.util.List;
import java.util.Map;

import org.antlr.runtime.tree.Tree;

/**
 * Executes a hash program by walking its AST with a context reference.
 * @author Thiago de Arruda
 *
 */
public class ProgramEvaluator extends LiteralEvaluator {

	private Scope context;

	public ProgramEvaluator(Scope context) {
		this.context = context;
	}

	@Override
	protected Tree visitFunction(Tree node, Tree parameters, Tree block) {
		List params = (List) ((Result) visit(parameters)).getEvaluationResult();
		return new Result(new FunctionEvaluator(context, params, block));
	}

	@Override
	protected Tree visitReturn(Tree node, Tree returnExpression) {
		throw new ReturnStatement(
				((Result) visit(returnExpression)).getEvaluationResult());
	}

	@Override
	protected Tree visitBlock(Tree node) {
		int len = node.getChildCount();
		Object lastEvaluatedExpression = null;
		for (int i = 0; i < len; i++)
			lastEvaluatedExpression = ((Result) visit(node.getChild(i)))
					.getEvaluationResult();
		return new Result(lastEvaluatedExpression);
	}

	@Override
	protected Tree visitAssignment(Tree node, Tree target, Tree expression) {
		Object value = ((Result) visit(expression)).getEvaluationResult();
		if (target.getType() == ATTRIBUTE || target.getType() == INDEX) {
			Object ownerObject = ((Result) visit(target.getChild(0)))
					.getEvaluationResult();
			Object key = ((Result) visit(target.getChild(1)))
					.getEvaluationResult();
			if (target.getType() == ATTRIBUTE)
				Runtime.setAttribute(ownerObject, key, value);
			else
				Runtime.setIndex(ownerObject, key, value);
		} else
			// target is an identifier
			context.put(target.getText(), value);
		return new Result(value);
	}

	@Override
	protected Tree visitEvalAndIncrement(Tree node, Tree target, Tree assignment) {
		Object rv = ((Result) visit(target)).getEvaluationResult();
		visit(assignment);
		return new Result(rv);
	}

	@Override
	protected Tree visitBinaryExpression(Tree node, Tree left, Tree right) {
		Object l = ((Result) visit(left)).getEvaluationResult();
		Object r = ((Result) visit(right)).getEvaluationResult();
		return new Result(
				Runtime.invokeBinaryOperator(node.getText(), l, r));
	}

	@Override
	protected Tree visitUnaryExpression(Tree node, Tree operand) {
		String operatorTxt = node.getText();
		if (operatorTxt.equals("+"))// ignore
			return visit(operand);
		Object op = ((Result) visit(operand)).getEvaluationResult();
		return new Result(Runtime.invokeUnaryOperator(operatorTxt, op));
	}

	@Override
	protected Tree visitInvocation(Tree node, Tree expression, Tree arguments) {
		Object[] args = ((List) ((Result) visit(arguments))
				.getEvaluationResult()).toArray();
		if (expression.getType() == ATTRIBUTE || expression.getType() == INDEX) {
			// this is a method call
			Object tgt = ((Result) visit(expression.getChild(0)))
					.getEvaluationResult();
			Object methodKey = ((Result) visit(expression.getChild(1)))
					.getEvaluationResult();
			return new Result(Runtime.invokeNormalMethod(tgt, methodKey, args));
		} else {		
			// normal function call
			Object exp = ((Result) visit(expression)).getEvaluationResult();
			return new Result(Runtime.invokeFunction(exp, args));
		}
	}

	@Override
	protected Tree visitMap(Tree node) {
		Map rv = Factory.createMap();
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
		return new Result(Runtime.getAttribute(tgt, key));
	}

	@Override
	protected Tree visitIndexAccess(Tree node, Tree target, Tree itemKey) {
		Object tgt = ((Result) visit(target)).getEvaluationResult();
		Object key = ((Result) visit(itemKey)).getEvaluationResult();
		return new Result(Runtime.getIndex(tgt, key));
	}

	@Override
	protected Tree visitSlice(Tree node, Tree target, Tree sliceArgs) {
		Object tgt = ((Result) visit(target)).getEvaluationResult();
		List args = (List) ((Result) visit(sliceArgs)).getEvaluationResult();
		return new Result(Runtime.getSlice(tgt, args.get(0), args.get(1),
				args.get(2)));
	}

	@Override
	protected Tree visitIdentifier(Tree node) {
		return new Result(context.get(node.getText()));
	}
}
