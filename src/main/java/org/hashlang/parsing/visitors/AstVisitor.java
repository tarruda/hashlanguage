package org.hashlang.parsing.visitors;

import static org.hashlang.parsing.HashParser.ASSIGN;
import static org.hashlang.parsing.HashParser.ATTRIBUTE;
import static org.hashlang.parsing.HashParser.BINARY;
import static org.hashlang.parsing.HashParser.BLOCK;
import static org.hashlang.parsing.HashParser.BOOLEAN;
import static org.hashlang.parsing.HashParser.BREAK;
import static org.hashlang.parsing.HashParser.CONDITIONAL;
import static org.hashlang.parsing.HashParser.CONTINUE;
import static org.hashlang.parsing.HashParser.DO;
import static org.hashlang.parsing.HashParser.FLOAT;
import static org.hashlang.parsing.HashParser.FOR;
import static org.hashlang.parsing.HashParser.FOREACH;
import static org.hashlang.parsing.HashParser.FUNCTION;
import static org.hashlang.parsing.HashParser.FUNCTIONBLOCK;
import static org.hashlang.parsing.HashParser.IDENTIFIER;
import static org.hashlang.parsing.HashParser.IF;
import static org.hashlang.parsing.HashParser.INCR;
import static org.hashlang.parsing.HashParser.INDEX;
import static org.hashlang.parsing.HashParser.INTEGER;
import static org.hashlang.parsing.HashParser.INVOCATION;
import static org.hashlang.parsing.HashParser.JUMPTO;
import static org.hashlang.parsing.HashParser.LIST;
import static org.hashlang.parsing.HashParser.MAP;
import static org.hashlang.parsing.HashParser.NAMEREF;
import static org.hashlang.parsing.HashParser.NULL;
import static org.hashlang.parsing.HashParser.REGEX;
import static org.hashlang.parsing.HashParser.RETURN;
import static org.hashlang.parsing.HashParser.RUNTIME_INVOCATION;
import static org.hashlang.parsing.HashParser.SLICE;
import static org.hashlang.parsing.HashParser.STRING;
import static org.hashlang.parsing.HashParser.THIS;
import static org.hashlang.parsing.HashParser.THROW;
import static org.hashlang.parsing.HashParser.TRY;
import static org.hashlang.parsing.HashParser.UNARY;
import static org.hashlang.parsing.HashParser.UNPACK_ASSIGN;
import static org.hashlang.parsing.HashParser.WHILE;
import static org.hashlang.parsing.HashParser.YIELD;

import org.antlr.runtime.tree.Tree;
import org.hashlang.parsing.exceptions.TreeValidationException;
import org.hashlang.parsing.tree.CommonHashAdaptor;
import org.hashlang.parsing.tree.CommonHashNode;
import org.hashlang.parsing.tree.HashNode;
import org.hashlang.parsing.tree.RuntimeInvocation;
import org.hashlang.util.Err;

/**
 * Base for all classes that need to do something with the AST. It can be used
 * for translation, analysis, compilation or any other processing of the AST.
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
			return visitLoop(node,
					CommonHashAdaptor.createBlock(node.getChild(0)),
					(HashNode) node.getChild(1),
					CommonHashAdaptor.createBlock(node.getChild(2)),
					(HashNode) node.getChild(3));
		case WHILE:
			return visitLoop(node, null, (HashNode) node.getChild(0), null,
					(HashNode) node.getChild(1));
		case DO:
			return visitLoop(node, (HashNode) node.getChild(1),
					(HashNode) node.getChild(0), null,
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
		case IDENTIFIER:
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
		HashNode setNext = null;
		if (foreachControl.getChildCount() == 0) {
			setNext = new CommonHashNode(ASSIGN);
			setNext.addChild(new CommonHashNode(NAMEREF, foreachControl
					.getText()));
		} else {
			setNext = new CommonHashNode(UNPACK_ASSIGN);
			setNext.addChild(foreachControl);
		}
		setNext.addChild(new RuntimeInvocation(RuntimeInvocation.ITERATOR_NEXT,
				iteratorVar));
		action.insertChild(0, setNext);
		return visitLoop(node, CommonHashAdaptor.createBlock(init), condition,
				null, action);
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
		HashNode target = (org.hashlang.parsing.tree.HashNode) node.getChild(0);
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
