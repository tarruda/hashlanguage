package hash.parsing.visitors;

import static hash.parsing.HashParser.*;

import org.antlr.runtime.tree.Tree;

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
