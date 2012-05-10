package hash.parsing.visitors;

import static hash.parsing.HashParser.BINARY;
import static hash.parsing.HashParser.BOOLEAN;
import static hash.parsing.HashParser.FLOAT;
import static hash.parsing.HashParser.INTEGER;
import static hash.parsing.HashParser.STRING;
import static hash.parsing.HashParser.UNARY;

import org.antlr.runtime.tree.Tree;

/**
 * Base class for all classes that need to process the AST. It can be used for
 * translation, transformation, analysis, compilation, execution or any other
 * processing of the AST.
 * 
 * The default behavior for the visitor methods is to simply return the 'Tree'
 * instance passed as the first argument.
 * 
 * @author Thiago de Arruda
 * 
 */
public abstract class AstVisitor {

	public Tree visit(Tree node) {
		int nodeType = node.getType();
		switch (nodeType) {
		case BINARY:
			return visitBinaryExpression(node, node.getChild(0),
					node.getChild(1));
		case UNARY:
			return visitUnaryExpression(node, node.getChild(0));
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

	protected Tree visitBinaryExpression(Tree operator, Tree left, Tree right) {
		return operator;
	}

	protected Tree visitUnaryExpression(Tree operator, Tree operand) {
		return operator;
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
