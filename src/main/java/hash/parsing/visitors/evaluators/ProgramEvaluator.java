package hash.parsing.visitors.evaluators;

import static hash.parsing.HashParser.ATTRIBUTE;
import static hash.parsing.HashParser.INDEX;
import hash.lang.Context;
import hash.parsing.tree.HashNode;
import hash.parsing.tree.Result;
import hash.runtime.Factory;
import hash.runtime.Runtime;
import hash.util.Constants;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.antlr.runtime.tree.Tree;

/**
 * Executes a hash program by walking its AST with a context reference.
 * 
 * @author Thiago de Arruda
 * 
 */
public class ProgramEvaluator extends LiteralEvaluator {
	// TODO Need to push line numbers/filenames to a stack in order to produce
	// meaningful traces when exceptions are thrown

	private Context context;

	public ProgramEvaluator(Context context) {
		this.context = context;
	}

	@Override
	protected Tree visitIf(Tree node, Tree condition, Tree ifTrue, Tree ifFalse) {
		Object cond = ((Result) visit(condition)).getNodeData();
		Object result = null;
		if ((Boolean) Runtime.invokeNormalMethod(cond, Constants.BOOLEAN_VALUE))
			result = ((Result) visit(ifTrue)).getNodeData();
		else
			result = ((Result) visit(ifFalse)).getNodeData();
		return new Result(result);
	}

	@Override
	protected Tree visitTryStatement(Tree node, Tree tryBlock,
			Tree catchBlocks, Tree finallyBlock) {
		Object lastEvaluatedExpression = null;
		Exception exception = null;
		try {
			lastEvaluatedExpression = ((Result) visit(tryBlock)).getNodeData();
		} catch (Exception ex) {
			boolean handled = false;
			int catchBlocksLen = catchBlocks.getChildCount();
			for (int i = 0; i < catchBlocksLen; i++) {
				Tree catchBlock = catchBlocks.getChild(i);
				Object exceptionType = ((Result) visit(catchBlock.getChild(0)))
						.getNodeData();
				if (exceptionType != null
						&& !Runtime.isInstance(ex, exceptionType))
					continue;
				String id = catchBlock.getChild(1).getText();
				context.put(id, ex);
				lastEvaluatedExpression = ((Result) visit(catchBlock
						.getChild(2))).getNodeData();
				handled = true;
				break;
			}
			if (!handled)
				exception = ex;
		} finally {
			if (finallyBlock != null)
				lastEvaluatedExpression = ((Result) visit(finallyBlock))
						.getNodeData();
			if (exception != null)
				throw new RuntimeException(exception);
		}
		return new Result(lastEvaluatedExpression);
	}

	@Override
	protected Tree visitThrow(Tree node, Tree throwableExpression) {
		Object throwable = ((Result) visit(throwableExpression)).getNodeData();
		throw Runtime.throwObj(throwable);
	}

	@Override
	protected Tree visitFunction(Tree node, Tree parameters, Tree block) {
		boolean isMethod = false;
		HashNode fNode = (HashNode) node;
		isMethod = fNode.getNodeData(HashNode.IS_METHOD) == Boolean.TRUE;
		List params = (List) ((Result) visit(parameters)).getNodeData();
		return new Result(new FunctionEvaluator(context, params, block,
				isMethod));
	}

	@Override
	protected Tree visitReturn(Tree node, Tree returnExpression) {
		throw new ReturnStatement(
				((Result) visit(returnExpression)).getNodeData());
	}

	@Override
	protected Tree visitBlock(Tree node) {
		int len = node.getChildCount();
		Object lastEvaluatedExpression = null;
		for (int i = 0; i < len; i++)
			lastEvaluatedExpression = ((Result) visit(node.getChild(i)))
					.getNodeData();
		return new Result(lastEvaluatedExpression);
	}

	@Override
	protected Tree visitAssignment(Tree node, Tree target, Tree expression) {
		Object value = ((Result) visit(expression)).getNodeData();
		if (target.getType() == ATTRIBUTE || target.getType() == INDEX) {
			Object ownerObject = ((Result) visit(target.getChild(0)))
					.getNodeData();
			Object key = ((Result) visit(target.getChild(1))).getNodeData();
			if (target.getType() == ATTRIBUTE)
				Runtime.setAttribute(ownerObject, key, value);
			else
				Runtime.setIndex(ownerObject, key, value);
		} else {
			// target is an identifier
			Context c = getContext((HashNode) target);
			c.put(target.getText(), value);
		}
		return new Result(value);
	}

	@Override
	protected Tree visitEvalAndIncrement(Tree node, Tree target, Tree assignment) {
		Object rv = ((Result) visit(target)).getNodeData();
		visit(assignment);
		return new Result(rv);
	}

	@Override
	protected Tree visitBinaryExpression(Tree node, Tree left, Tree right) {
		Object l = ((Result) visit(left)).getNodeData();
		Object r = ((Result) visit(right)).getNodeData();
		String text = node.getText();
		if (text.equals("is")) // skip the runtime
			return new Result(l == r);
		else
			return new Result(
					Runtime.invokeBinaryOperator(node.getText(), l, r));
	}

	@Override
	protected Tree visitUnaryExpression(Tree node, Tree operand) {
		String operatorTxt = node.getText();
		if (operatorTxt.equals("+"))// ignore
			return visit(operand);
		Object op = ((Result) visit(operand)).getNodeData();
		return new Result(Runtime.invokeUnaryOperator(operatorTxt, op));
	}

	@Override
	protected Tree visitInvocation(Tree node, Tree expression, Tree arguments) {
		Object[] args = ((List) ((Result) visit(arguments)).getNodeData())
				.toArray();
		if (expression.getType() == ATTRIBUTE || expression.getType() == INDEX) {
			// this is a method call
			Object tgt = ((Result) visit(expression.getChild(0))).getNodeData();
			Object methodKey = ((Result) visit(expression.getChild(1)))
					.getNodeData();
			return new Result(Runtime.invokeNormalMethod(tgt, methodKey, args));
		} else {
			// normal function call
			Object exp = ((Result) visit(expression)).getNodeData();
			return new Result(Runtime.invokeFunction(exp, args));
		}
	}

	@Override
	protected Tree visitMap(Tree node) {
		Map rv = Factory.createMap();
		int len = node.getChildCount();
		for (int i = 0; i < len; i++) {
			Object key = ((Result) visit(node.getChild(i))).getNodeData();
			Object value = ((Result) visit(node.getChild(i).getChild(0)))
					.getNodeData();
			rv.put(key, value);
		}
		return new Result(rv);
	}

	@Override
	protected Tree visitList(Tree node) {
		int len = node.getChildCount();
		List rv = Factory.createList();
		for (int i = 0; i < len; i++)
			rv.add(((Result) visit(node.getChild(i))).getNodeData());
		return new Result(rv);
	}

	@Override
	protected Tree visitAttributeAccess(Tree node, Tree target,
			Tree attributeKey) {
		Object tgt = ((Result) visit(target)).getNodeData();
		Object key = ((Result) visit(attributeKey)).getNodeData();
		return new Result(Runtime.getAttribute(tgt, key));
	}

	@Override
	protected Tree visitIndexAccess(Tree node, Tree target, Tree itemKey) {
		Object tgt = ((Result) visit(target)).getNodeData();
		Object key = ((Result) visit(itemKey)).getNodeData();
		return new Result(Runtime.getIndex(tgt, key));
	}

	@Override
	protected Tree visitSlice(Tree node, Tree target, Tree sliceArgs) {
		Object tgt = ((Result) visit(target)).getNodeData();
		List args = (List) ((Result) visit(sliceArgs)).getNodeData();
		return new Result(Runtime.getSlice(tgt, args.get(0), args.get(1),
				args.get(2)));
	}

	@Override
	protected Tree visitIdentifier(Tree node) {
		Context c = getContext((HashNode) node);
		return new Result(c.get(node.getText()));
	}

	@Override
	protected Tree visitRegex(Tree node) {
		String regexLiteral = node.getText();
		String regexText = regexLiteral.substring(1,
				regexLiteral.lastIndexOf('/'));
		int flags = 0;
		if (regexLiteral.endsWith("i"))
			flags = Pattern.CASE_INSENSITIVE;
		return new Result(Pattern.compile(regexText, flags));
	}

	@Override
	protected Tree visitThis(Tree node) {
		return visitIdentifier(node);
	}

	private Context getContext(HashNode identifier) {
		int level = 0;
		if (identifier.getNodeData(HashNode.CONTEXT_LEVEL) != null)
			level = (Integer) identifier.getNodeData(HashNode.CONTEXT_LEVEL);
		Context c = context;
		while (level > 0 && c.getParent() != null)
			c = c.getParent();
		return c;
	}
}
