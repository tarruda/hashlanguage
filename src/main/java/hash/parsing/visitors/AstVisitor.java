package hash.parsing.visitors;

import static hash.parsing.HashParser.ASSIGN;
import static hash.parsing.HashParser.ATTRIBUTE;
import static hash.parsing.HashParser.BINARY;
import static hash.parsing.HashParser.BOOLEAN;
import static hash.parsing.HashParser.FLOAT;
import static hash.parsing.HashParser.IDENTIFIER;
import static hash.parsing.HashParser.INTEGER;
import static hash.parsing.HashParser.INVOCATION;
import static hash.parsing.HashParser.ITEM;
import static hash.parsing.HashParser.LIST;
import static hash.parsing.HashParser.OBJECT;
import static hash.parsing.HashParser.STRING;
import static hash.parsing.HashParser.UNARY;
import hash.parsing.exceptions.TreeWalkException;

import org.antlr.runtime.tree.Tree;

/**
 * Base class for all classes that need to do something with the AST. It can be
 * used for translation, transformation, analysis, compilation, execution or any
 * other processing of the AST.
 * 
 * The default behavior for the visitor methods is to simply return the 'Tree'
 * instance passed as the first argument.
 * 
 * @author Thiago de Arruda
 * 
 */
public abstract class AstVisitor {

	public final Tree visit(Tree node) {
		int nodeType = node.getType();
		switch (nodeType) {
		case ASSIGN:
			Tree target = node.getChild(0);
			if (!(target.getType() == ATTRIBUTE || target.getType() == ITEM || target
					.getType() == IDENTIFIER))
				throw new TreeWalkException(target.getLine(),
						target.getCharPositionInLine(),
						"Assignment target must be an identifier, attribute or index");
			return visitAssignment(node, node.getChild(0), node.getChild(1));
		case BINARY:
			return visitBinaryExpression(node, node.getChild(0),
					node.getChild(1));
		case UNARY:
			return visitUnaryExpression(node, node.getChild(0));
		case ATTRIBUTE:
			return visitAttributeAccess(node, node.getChild(0),
					node.getChild(1));
		case ITEM:
			return visitItemAccess(node, node.getChild(0), node.getChild(1));
		case INVOCATION:
			return visitInvocation(node, node.getChild(0), node.getChild(1));
		case OBJECT:
			return visitObject(node);
		case LIST:
			return visitList(node);
		case IDENTIFIER:
			return visitIdentifier(node);
		case STRING:
			return visitString(node);
		case FLOAT:
			return visitFloat(node);
		case INTEGER:
			return visitInteger(node);
		case BOOLEAN:
			return visitBoolean(node);
		default:
			return node;
		}
	}

	protected Tree visitAssignment(Tree node, Tree target, Tree expression) {
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

	protected Tree visitObject(Tree node) {
		return node;
	}

	protected Tree visitList(Tree node) {
		return node;
	}

	protected Tree visitAttributeAccess(Tree node, Tree target,
			Tree attributeKey) {
		return node;
	}

	protected Tree visitItemAccess(Tree node, Tree target, Tree itemKey) {
		return node;
	}

	protected Tree visitIdentifier(Tree node) {
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
}
