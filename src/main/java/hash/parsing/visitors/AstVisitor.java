package hash.parsing.visitors;

import static hash.parsing.HashParser.ASSIGN;
import static hash.parsing.HashParser.ATTRIBUTE;
import static hash.parsing.HashParser.BINARY;
import static hash.parsing.HashParser.BLOCK;
import static hash.parsing.HashParser.BOOLEAN;
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
import static hash.parsing.HashParser.BREAK;
import static hash.parsing.HashParser.CONTINUE;
import static hash.parsing.HashParser.INVOCATION;
import static hash.parsing.HashParser.LIST;
import static hash.parsing.HashParser.MAP;
import static hash.parsing.HashParser.NAMEREF;
import static hash.parsing.HashParser.NULL;
import static hash.parsing.HashParser.YIELD;
import static hash.parsing.HashParser.RESUME;
import static hash.parsing.HashParser.REGEX;
import static hash.parsing.HashParser.RETURN;
import static hash.parsing.HashParser.SLICE;
import static hash.parsing.HashParser.STRING;
import static hash.parsing.HashParser.THIS;
import static hash.parsing.HashParser.THROW;
import static hash.parsing.HashParser.TRY;
import static hash.parsing.HashParser.UNARY;
import static hash.parsing.HashParser.WHILE;
import hash.parsing.exceptions.TreeValidationException;
import hash.parsing.tree.HashNode;

import org.antlr.runtime.tree.Tree;

/**
 * Base for all classes that need to do something with the AST. It can be used
 * for translation, transformation, analysis, compilation, execution or any
 * other processing of the AST.
 * 
 * The 'visit' method will validate tree structure and delegate further
 * processing to the specialized visitor methods.
 * 
 * The default behavior for the visitor methods is to simply return the 'Tree'
 * instance passed as the first argument. Subclasses can override this behavior
 * and do any kind of processing of the tree.
 * 
 * @author Thiago de Arruda
 * 
 */
public abstract class AstVisitor {

	public final Tree visit(Tree node) {
		int nodeType = node.getType();
		switch (nodeType) {
		case FOREACH:
			return visitForeach(node, node.getChild(0), node.getChild(1),
					node.getChild(2));
		case FOR:
			return visitFor(node, node.getChild(0), node.getChild(1),
					node.getChild(2), node.getChild(3));
		case WHILE:
			return visitWhile(node, node.getChild(0), node.getChild(1));
		case DO:
			return visitDoWhile(node, node.getChild(0), node.getChild(1));
		case IF:
			return visitIf(node, node.getChild(0), node.getChild(1),
					node.getChild(2));
		case TRY:
			return visitTryStatement(node, node.getChild(0), node.getChild(1),
					node.getChild(2));
		case THROW:
			return visitThrow(node, node.getChild(0));
		case FUNCTION:
			checkIfFunctionIsMethod((HashNode) node, node.getChild(1));
			return visitFunction(node, node.getChild(0), node.getChild(1));
		case RETURN:
			validateReturn(node);
			return visitReturn(node, node.getChild(0));
		case CONTINUE:
			validateContinue(node);
			return visitContinue(node);
		case BREAK:
			validateBreak(node);
			return visitBreak(node);
		case YIELD:
			return visitYield(node);
		case RESUME:
			return visitResume(node);
		case BLOCK:
		case FUNCTIONBLOCK:
			return visitBlock(node);
		case ASSIGN:
			validateAssignment(node);
			return visitAssignment(node, node.getChild(0), node.getChild(1));
		case INCR:
			return visitEvalAndIncrement(node, node.getChild(0),
					node.getChild(1));
		case BINARY:
			return visitBinaryExpression(node, node.getChild(0),
					node.getChild(1));
		case UNARY:
			return visitUnaryExpression(node, node.getChild(0));
		case ATTRIBUTE:
			return visitAttributeAccess(node, node.getChild(0),
					node.getChild(1));
		case INDEX:
			return visitIndexAccess(node, node.getChild(0), node.getChild(1));
		case SLICE:
			return visitSlice(node, node.getChild(0), node.getChild(1));
		case INVOCATION:
			return visitInvocation(node, node.getChild(0), node.getChild(1));
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

	protected Tree visitForeach(Tree node, Tree id, Tree iterable, Tree action) {
		return node;
	}

	protected Tree visitFor(Tree node, Tree init, Tree condition, Tree update,
			Tree action) {
		return node;
	}

	protected Tree visitWhile(Tree node, Tree condition, Tree action) {
		return node;
	}

	protected Tree visitDoWhile(Tree node, Tree condition, Tree action) {
		return node;
	}

	protected Tree visitIf(Tree node, Tree condition, Tree trueAction,
			Tree falseAction) {
		return node;
	}

	protected Tree visitTryStatement(Tree node, Tree tryBlock,
			Tree catchBlocks, Tree finallyBlock) {
		return node;
	}

	protected Tree visitThrow(Tree node, Tree throwableExpression) {
		return node;
	}

	protected Tree visitFunction(Tree node, Tree parameters, Tree block) {
		return node;
	}

	protected Tree visitReturn(Tree node, Tree returnExpression) {
		return node;
	}

	protected Tree visitBreak(Tree node) {
		return node;
	}

	protected Tree visitResume(Tree node) {
		return node;
	}

	private Tree visitYield(Tree node) {
		throw new TreeValidationException(node.getLine(),
				node.getCharPositionInLine(),
				"Yield expression can only exist inside a function");
	}

	protected Tree visitContinue(Tree node) {
		return node;
	}

	protected Tree visitBlock(Tree node) {
		return node;
	}

	protected Tree visitAssignment(Tree node, Tree target, Tree expression) {
		return node;
	}

	protected Tree visitEvalAndIncrement(Tree node, Tree target, Tree assignment) {
		return node;
	}

	protected Tree visitBinaryExpression(Tree node, Tree left, Tree right) {
		return node;
	}

	protected Tree visitUnaryExpression(Tree node, Tree operand) {
		return node;
	}

	protected Tree visitInvocation(Tree node, Tree target, Tree args) {
		return node;
	}

	protected Tree visitMap(Tree node) {
		return node;
	}

	protected Tree visitList(Tree node) {
		return node;
	}

	protected Tree visitAttributeAccess(Tree node, Tree target,
			Tree attributeKey) {
		return node;
	}

	protected Tree visitIndexAccess(Tree node, Tree target, Tree itemKey) {
		return node;
	}

	protected Tree visitSlice(Tree node, Tree target, Tree sliceArgs) {
		return node;
	}

	protected Tree visitNameReference(Tree node) {
		return node;
	}

	protected Tree visitThis(Tree node) {
		return node;
	}

	protected Tree visitRegex(Tree node) {
		return node;
	}

	protected Tree visitString(Tree node) {
		return node;
	}

	protected Tree visitFloat(Tree node) {
		return node;
	}

	protected Tree visitInteger(Tree node) {
		return node;
	}

	protected Tree visitBoolean(Tree node) {
		return node;
	}

	protected Tree visitNull(Tree node) {
		return node;
	}

	private void validateReturn(Tree node) {
		// Return statement must be inside a function
		boolean insideFunction = false;
		Tree current = node;
		while ((current = current.getParent()) != null && !insideFunction)
			insideFunction = current.getType() == FUNCTIONBLOCK;
		if (!insideFunction)
			throw new TreeValidationException(node.getLine(),
					node.getCharPositionInLine(),
					"Return statement can only exist inside a function");
	}

	private void validateBreak(Tree node) {
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

	private void validateContinue(Tree node) {
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

	private void validateAssignment(Tree node) {
		Tree target = node.getChild(0);
		if (!(target.getType() == ATTRIBUTE || target.getType() == INDEX || target
				.getType() == NAMEREF))
			throw new TreeValidationException(target.getLine(),
					target.getCharPositionInLine(),
					"Assignment target must be an identifier, attribute or index");
	}

	private void checkIfFunctionIsMethod(HashNode node, Tree block) {
		// here if verify if the function body contains any reference to 'this'.
		// if so, we mark the function as a method(it can only be invoked with
		// the first implicit argument)
		boolean found = searchThisExpression(block);
		if (found)
			node.setNodeData(HashNode.IS_METHOD, true);
	}

	private boolean searchThisExpression(Tree current) {
		if (current.getType() == FUNCTION)
			// we dont want to descend into closures
			return false;
		if (current.getType() == THIS)
			return true;
		int len = current.getChildCount();
		boolean rv = false;
		for (int i = 0; !rv && i < len; i++)
			rv = rv || searchThisExpression(current.getChild(i));
		return rv;
	}

}
