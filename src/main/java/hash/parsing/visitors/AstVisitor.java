package hash.parsing.visitors;

import static hash.parsing.HashParser.ASSIGN;
import static hash.parsing.HashParser.ATTRIBUTE;
import static hash.parsing.HashParser.BINARY;
import static hash.parsing.HashParser.BLOCK;
import static hash.parsing.HashParser.BOOLEAN;
import static hash.parsing.HashParser.BREAK;
import static hash.parsing.HashParser.CONDITIONAL;
import static hash.parsing.HashParser.CONTINUE;
import static hash.parsing.HashParser.DO;
import static hash.parsing.HashParser.FLOAT;
import static hash.parsing.HashParser.FOR;
import static hash.parsing.HashParser.FOREACH;
import static hash.parsing.HashParser.FUNCTION;
import static hash.parsing.HashParser.FUNCTIONBLOCK;
import static hash.parsing.HashParser.IF;
import static hash.parsing.HashParser.INCR;
import static hash.parsing.HashParser.INDEX;
import static hash.parsing.HashParser.INTEGER;
import static hash.parsing.HashParser.INVOCATION;
import static hash.parsing.HashParser.JUMPTO;
import static hash.parsing.HashParser.LIST;
import static hash.parsing.HashParser.MAP;
import static hash.parsing.HashParser.NAMEREF;
import static hash.parsing.HashParser.NULL;
import static hash.parsing.HashParser.REGEX;
import static hash.parsing.HashParser.RETURN;
import static hash.parsing.HashParser.RUNTIME_INVOCATION;
import static hash.parsing.HashParser.SLICE;
import static hash.parsing.HashParser.STRING;
import static hash.parsing.HashParser.THIS;
import static hash.parsing.HashParser.THROW;
import static hash.parsing.HashParser.TRY;
import static hash.parsing.HashParser.UNARY;
import static hash.parsing.HashParser.UNPACK_ASSIGN;
import static hash.parsing.HashParser.WHILE;
import static hash.parsing.HashParser.YIELD;
import hash.parsing.exceptions.TreeValidationException;
import hash.parsing.tree.CommonHashNode;
import hash.parsing.tree.HashNode;
import hash.parsing.tree.RuntimeInvocation;
import hash.util.Err;

import org.antlr.runtime.tree.Tree;

/**
 * Base for all classes that need to do something with the AST. It can be used
 * for translation, transformation, analysis, compilation, execution or any
 * other processing of the AST.
 * 
 * The 'visit' method will validate tree structure and delegate further
 * processing to the specialized visitor methods.
 * 
 * The default behavior for the visitor methods is to simply return the
 * 'HashNode' instance passed as the first argument. Subclasses can override
 * this behavior and do any kind of processing of the tree.
 * 
 * @author Thiago de Arruda
 * 
 */
public abstract class AstVisitor {
	private static final String FOREACH_NESTING = "ForeachNesting";

	public final HashNode visit(HashNode node) {
		int nodeType = node.getType();
		switch (nodeType) {
		case FOREACH:
			return visitForeach(node, (HashNode) node.getChild(0),
					(HashNode) node.getChild(1), (HashNode) node.getChild(2));
		case FOR:
			return visitLoop(node, (HashNode) node.getChild(0),
					(HashNode) node.getChild(1), (HashNode) node.getChild(2),
					(HashNode) node.getChild(3));
		case WHILE:
			return visitLoop(node, null, (HashNode) node.getChild(0), null,
					(HashNode) node.getChild(1));
		case DO:
			return visitDoWhile(node, (HashNode) node.getChild(0),
					(HashNode) node.getChild(1));
		case IF:
			return visitIf(node, (HashNode) node.getChild(0),
					(HashNode) node.getChild(1), (HashNode) node.getChild(2));
		case TRY:
			return visitTryStatement(node, (HashNode) node.getChild(0),
					(HashNode) node.getChild(1), (HashNode) node.getChild(2));
		case THROW:
			return visitThrow(node, (HashNode) node.getChild(0));
		case FUNCTION:
			markFunctionAsMethodOrContinuation(node,
					(HashNode) node.getChild(1));
			return visitFunction((HashNode) node, (HashNode) node.getChild(0),
					(HashNode) node.getChild(1));
		case RETURN:
			validateReturnAndYield(node);
			return visitReturn(node, (HashNode) node.getChild(0));
		case CONTINUE:
			validateContinue(node);
			return visitContinue(node);
		case BREAK:
			validateBreak(node);
			return visitBreak(node);
		case YIELD:
			validateReturnAndYield(node);
			return visitYield(node, (HashNode) node.getChild(0));
		case JUMPTO:
			return visitJump(node, (HashNode) node.getChild(0),
					(HashNode) node.getChild(1));
		case BLOCK:
		case FUNCTIONBLOCK:
			return visitBlock(node);
		case ASSIGN:
			validateAssignment(node);
			return visitAssignment(node, (HashNode) node.getChild(0),
					(HashNode) node.getChild(1));
		case UNPACK_ASSIGN:
			return visitUnpackAssign(node, (HashNode) node.getChild(0),
					(HashNode) node.getChild(1));
		case INCR:
			return visitEvalAndIncrement(node, (HashNode) node.getChild(0),
					(HashNode) node.getChild(1));
		case CONDITIONAL:
			return visitConditionalExpression(node,
					(HashNode) node.getChild(0), (HashNode) node.getChild(1),
					(HashNode) node.getChild(2));
		case BINARY:
			return visitBinaryExpression(node, (HashNode) node.getChild(0),
					(HashNode) node.getChild(1));
		case UNARY:
			return visitUnaryExpression(node, (HashNode) node.getChild(0));
		case ATTRIBUTE:
			return visitAttributeAccess(node, (HashNode) node.getChild(0),
					(HashNode) node.getChild(1));
		case INDEX:
			return visitIndexAccess(node, (HashNode) node.getChild(0),
					(HashNode) node.getChild(1));
		case SLICE:
			return visitSlice(node, (HashNode) node.getChild(0),
					(HashNode) node.getChild(1));
		case INVOCATION:
			return visitInvocation(node, (HashNode) node.getChild(0),
					(HashNode) node.getChild(1));
		case RUNTIME_INVOCATION:
			return visitRuntimeInvocation((RuntimeInvocation) node);
		case MAP:
			return visitMap(node);
		case LIST:
			return visitList(node);
		case NAMEREF:
			return visitNameReference(node);
		case THIS:
			return visitThis(node);
		case REGEX:
			return visitRegex(node);
		case STRING:
			return visitString(node);
		case FLOAT:
			return visitFloat(node);
		case INTEGER:
			return visitInteger(node);
		case BOOLEAN:
			return visitBoolean(node);
		case NULL:
			return visitNull(node);
		default:
			return node;
		}
	}

	protected HashNode visitLoop(HashNode node, HashNode init,
			HashNode condition, HashNode update, HashNode action) {
		return node;
	}

	protected final HashNode visitForeach(HashNode node,
			HashNode foreachControl, HashNode iterable, HashNode action) {
		int nestingLevel = 0;
		HashNode current = node;
		while (current.getParent() != null && nestingLevel == 0) {
			// Find if this is a nested foreach. If so, the variable that will
			// store the iterator must be diferent than the one used by the
			// parent foreach. For disambiguating we append the nesting level
			// to the variable name
			current = (HashNode) current.getParent();
			if (current.getType() == FOREACH)
				nestingLevel = (Integer) current.getNodeData(FOREACH_NESTING) + 1;
		}
		node.setNodeData(FOREACH_NESTING, nestingLevel);
		String varName = "**iter**" + nestingLevel;
		HashNode iteratorVar = new CommonHashNode(NAMEREF, varName);
		HashNode init = new CommonHashNode(ASSIGN);
		init.addChild(iteratorVar);
		init.addChild(new RuntimeInvocation(RuntimeInvocation.GET_ITERATOR,
				iterable));
		HashNode condition = new RuntimeInvocation(
				RuntimeInvocation.ITERATOR_HASNEXT, iteratorVar);
		HashNode setNext = new CommonHashNode(ASSIGN);
		setNext.addChild(new CommonHashNode(NAMEREF, foreachControl.getText()));
		setNext.addChild(new RuntimeInvocation(RuntimeInvocation.ITERATOR_NEXT,
				iteratorVar));
		action.insertChild(0, setNext);
		return visitLoop(node, init, condition, null, action);
	}

	protected HashNode visitDoWhile(HashNode node, HashNode condition,
			HashNode action) {
		return node;
	}

	protected HashNode visitIf(HashNode node, HashNode condition,
			HashNode trueAction, HashNode falseAction) {
		return node;
	}

	protected HashNode visitTryStatement(HashNode node, HashNode tryBlock,
			HashNode catchBlocks, HashNode finallyBlock) {
		return node;
	}

	protected HashNode visitThrow(HashNode node, HashNode throwableExpression) {
		return node;
	}

	protected HashNode visitFunction(HashNode node, HashNode parameters,
			HashNode block) {
		return node;
	}

	protected HashNode visitReturn(HashNode node, HashNode returnExpression) {
		return node;
	}

	protected HashNode visitBreak(HashNode node) {
		return node;
	}

	protected HashNode visitJump(HashNode node, HashNode target, HashNode arg) {
		return node;
	}

	protected HashNode visitYield(HashNode node, HashNode yieldExpression) {
		return node;
	}

	protected HashNode visitContinue(HashNode node) {
		return node;
	}

	protected HashNode visitBlock(HashNode node) {
		return node;
	}

	protected HashNode visitAssignment(HashNode node, HashNode target,
			HashNode expression) {
		return node;
	}

	protected final HashNode visitUnpackAssign(HashNode node,
			HashNode targetList, HashNode expression) {
		int childCount = targetList.getChildCount();
		HashNode firstTarget = (HashNode) targetList.getChild(0);
		HashNode rv = visitAssignment(null, firstTarget, expression);
		for (int i = childCount - 1; i >= 0; i--) {
			HashNode target = (HashNode) targetList.getChild(i);
			HashNode index = new CommonHashNode(INDEX);
			HashNode indexNo = new CommonHashNode(INTEGER);
			index.addChild(firstTarget);
			index.addChild(indexNo);
			indexNo.setText(Integer.toHexString(i));
			visitAssignment(null, target, index);
			pop();
		}
		return rv;
	}

	protected HashNode visitEvalAndIncrement(HashNode node, HashNode target,
			HashNode assignment) {
		return node;
	}

	protected HashNode visitConditionalExpression(HashNode node,
			HashNode condition, HashNode trueValue, HashNode falseValue) {
		return node;
	}

	protected HashNode visitBinaryExpression(HashNode node, HashNode left,
			HashNode right) {
		return node;
	}

	protected HashNode visitUnaryExpression(HashNode node, HashNode operand) {
		return node;
	}

	protected HashNode visitInvocation(HashNode node, HashNode target,
			HashNode args) {
		return node;
	}

	protected HashNode visitRuntimeInvocation(RuntimeInvocation node) {
		return node;
	}

	protected HashNode visitMap(HashNode node) {
		return node;
	}

	protected HashNode visitList(HashNode node) {
		return node;
	}

	protected HashNode visitAttributeAccess(HashNode node, HashNode target,
			HashNode attributeKey) {
		return node;
	}

	protected HashNode visitIndexAccess(HashNode node, HashNode target,
			HashNode itemKey) {
		return node;
	}

	protected HashNode visitSlice(HashNode node, HashNode target,
			HashNode sliceArgs) {
		return node;
	}

	protected HashNode visitNameReference(HashNode node) {
		return node;
	}

	protected HashNode visitThis(HashNode node) {
		return node;
	}

	protected HashNode visitRegex(HashNode node) {
		return node;
	}

	protected HashNode visitString(HashNode node) {
		return node;
	}

	protected HashNode visitFloat(HashNode node) {
		return node;
	}

	protected HashNode visitInteger(HashNode node) {
		return node;
	}

	protected HashNode visitBoolean(HashNode node) {
		return node;
	}

	protected HashNode visitNull(HashNode node) {
		return node;
	}

	protected void pop() {
		throw Err.notImplemented();
	}

	private void validateReturnAndYield(HashNode node) {
		// Return statement must be inside a function
		boolean insideFunction = false;
		Tree current = node;
		while ((current = current.getParent()) != null && !insideFunction)
			insideFunction = current.getType() == FUNCTIONBLOCK;
		if (!insideFunction)
			throw new TreeValidationException(node.getLine(),
					node.getCharPositionInLine(),
					"Return statement/yield expression can only exist inside a function");
	}

	private void validateBreak(HashNode node) {
		boolean insideLoop = false;
		Tree current = node;
		while ((current = current.getParent()) != null && !insideLoop)
			switch (current.getType()) {
			case DO:
			case WHILE:
			case FOR:
			case FOREACH:
				insideLoop = true;
			}
		if (!insideLoop)
			throw new TreeValidationException(node.getLine(),
					node.getCharPositionInLine(),
					"Break statement can only exist inside a loop/switch");
	}

	private void validateContinue(HashNode node) {
		boolean insideLoop = false;
		Tree current = node;
		while ((current = current.getParent()) != null && !insideLoop)
			switch (current.getType()) {
			case DO:
			case WHILE:
			case FOR:
			case FOREACH:
				insideLoop = true;
			}
		if (!insideLoop)
			throw new TreeValidationException(node.getLine(),
					node.getCharPositionInLine(),
					"Continue statement can only exist inside a loop");
	}

	private void validateAssignment(HashNode node) {
		HashNode target = (hash.parsing.tree.HashNode) node.getChild(0);
		if (!(target.getType() == ATTRIBUTE || target.getType() == INDEX || target
				.getType() == NAMEREF))
			throw new TreeValidationException(target.getLine(),
					target.getCharPositionInLine(),
					"Assignment target must be an identifier, attribute or index");
	}

	private void markFunctionAsMethodOrContinuation(HashNode node,
			HashNode block) {
		// here if verify if the function body contains any reference to 'this'.
		// if so, we mark the function as a method(it can only be invoked with
		// the first implicit argument)
		boolean isMethod = searchExpressionType(block, THIS);
		boolean returnsContinuation = searchExpressionType(block, YIELD, JUMPTO);
		if (isMethod)
			node.setNodeData(HashNode.IS_METHOD, true);
		if (returnsContinuation)
			node.setNodeData(HashNode.RETURNS_CONTINUATION, true);
	}

	private boolean searchExpressionType(HashNode current, int... types) {
		if (current.getType() == FUNCTION)
			// we dont want to descend into closures
			return false;
		for (int i = 0; i < types.length; i++) {
			if (current.getType() == types[i])
				return true;
		}
		int len = current.getChildCount();
		boolean rv = false;
		for (int i = 0; !rv && i < len; i++)
			rv = rv
					|| searchExpressionType((HashNode) current.getChild(i),
							types);
		return rv;
	}

}
