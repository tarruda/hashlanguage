package hash.parsing.visitors;

import hash.parsing.tree.HashNode;

/**
 * This will transverse the entire tree, forcing validation on all nodes by the
 * 'AstVisitor' class. This class exists only for isolating the validation
 * tests, since the validation occurs automatically when an AstVisitor subclass
 * transverses the tree.
 * 
 * @author Thiago de Arruda
 * 
 */
public class AstValidator extends AstVisitor {
	@Override
	protected HashNode visitFunction(HashNode node, HashNode parameters,
			HashNode block) {
		visit(parameters);
		visit(block);
		return node;
	}

	@Override
	protected HashNode visitReturn(HashNode node, HashNode returnExpression) {
		visit(returnExpression);
		return node;
	}

	@Override
	protected HashNode visitBlock(HashNode node) {
		int len = node.getChildCount();
		for (int i = 0; i < len; i++)
			visit((HashNode) node.getChild(i));
		return node;
	}

	@Override
	protected HashNode visitAssignment(HashNode node, HashNode target,
			HashNode expression) {
		visit(target);
		visit(expression);
		return node;
	}

	@Override
	protected HashNode visitEvalAndIncrement(HashNode node, HashNode target,
			HashNode assignment) {
		visit(target);
		visit(assignment);
		return node;
	}

	@Override
	protected HashNode visitBinaryExpression(HashNode node, HashNode left,
			HashNode right) {
		visit(left);
		visit(right);
		return node;
	}

	@Override
	protected HashNode visitUnaryExpression(HashNode node, HashNode operand) {
		visit(node);
		visit(operand);
		return node;
	}

	@Override
	protected HashNode visitInvocation(HashNode node, HashNode expression,
			HashNode arguments) {
		visit(expression);
		visit(arguments);
		return node;
	}

	@Override
	protected HashNode visitMap(HashNode node) {
		int len = node.getChildCount();
		for (int i = 0; i < len; i++) {
			visit((HashNode) node.getChild(i));
			visit((HashNode) node.getChild(i));
		}
		return node;
	}

	@Override
	protected HashNode visitList(HashNode node) {
		int len = node.getChildCount();
		for (int i = 0; i < len; i++)
			visit((HashNode) node.getChild(i));
		return node;
	}

	@Override
	protected HashNode visitAttributeAccess(HashNode node, HashNode target,
			HashNode attributeKey) {
		visit(target);
		visit(attributeKey);
		return node;
	}

	@Override
	protected HashNode visitIndexAccess(HashNode node, HashNode target,
			HashNode itemKey) {
		visit(target);
		visit(itemKey);
		return node;
	}

	@Override
	protected HashNode visitSlice(HashNode node, HashNode target,
			HashNode sliceArgs) {
		visit(target);
		visit(sliceArgs);
		return node;
	}
}
