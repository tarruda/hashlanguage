package hash.parsing.visitors;

import org.antlr.runtime.tree.Tree;

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
	protected Tree visitFunction(Tree node, Tree parameters, Tree block) {
		visit(parameters);
		visit(block);
		return node;
	}

	@Override
	protected Tree visitReturn(Tree node, Tree returnExpression) {
		visit(returnExpression);
		return node;
	}

	@Override
	protected Tree visitBlock(Tree node) {
		int len = node.getChildCount();
		for (int i = 0; i < len; i++)
			visit(node.getChild(i));
		return node;
	}

	@Override
	protected Tree visitAssignment(Tree node, Tree target, Tree expression) {
		visit(target);
		visit(expression);
		return node;
	}

	@Override
	protected Tree visitEvalAndIncrement(Tree node, Tree target, Tree assignment) {
		visit(target);
		visit(assignment);
		return node;
	}

	@Override
	protected Tree visitBinaryExpression(Tree node, Tree left, Tree right) {
		visit(left);
		visit(right);
		return node;
	}

	@Override
	protected Tree visitUnaryExpression(Tree node, Tree operand) {
		visit(node);
		visit(operand);
		return node;
	}

	@Override
	protected Tree visitInvocation(Tree node, Tree expression, Tree arguments) {
		visit(expression);
		visit(arguments);
		return node;
	}

	@Override
	protected Tree visitMap(Tree node) {
		int len = node.getChildCount();
		for (int i = 0; i < len; i++) {
			visit(node.getChild(i));
			visit(node.getChild(i));
		}
		return node;
	}

	@Override
	protected Tree visitList(Tree node) {
		int len = node.getChildCount();
		for (int i = 0; i < len; i++)
			visit(node.getChild(i));
		return node;
	}

	@Override
	protected Tree visitAttributeAccess(Tree node, Tree target,
			Tree attributeKey) {
		visit(target);
		visit(attributeKey);
		return node;
	}

	@Override
	protected Tree visitIndexAccess(Tree node, Tree target, Tree itemKey) {
		visit(target);
		visit(itemKey);
		return node;
	}

	@Override
	protected Tree visitSlice(Tree node, Tree target, Tree sliceArgs) {
		visit(target);
		visit(sliceArgs);
		return node;
	}
}
