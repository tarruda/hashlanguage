package hash.parsing.visitors.evaluators;

import hash.parsing.tree.Result;
import hash.parsing.visitors.AstVisitor;

import org.antlr.runtime.tree.Tree;

public class LiteralEvaluator extends AstVisitor {
	public void advance(Integer n) {
		System.out.println("def");
		try {
			System.out.println(n.toString());
		} catch (NullPointerException e) {
			System.out.println(e);
		} finally {
			System.err.println("finally");
		}
	}

	@Override
	protected Tree visitInteger(Tree node) {
		String txt = node.getText();
		Long val = Long.parseLong(txt, 16);
		int iVal = val.intValue();
		if (val.longValue() == iVal)
			return new Result(iVal);
		return new Result(val);
	}

	@Override
	protected Tree visitFloat(Tree node) {
		String txt = node.getText();
		Double val = Double.parseDouble(txt);
		return new Result(val);
	}

	@Override
	protected Tree visitString(Tree node) {
		return new Result(node.getText());
	}

	@Override
	protected Tree visitBoolean(Tree node) {
		String txt = node.getText();
		return new Result(Boolean.parseBoolean(txt));
	}

	@Override
	protected Tree visitNull(Tree node) {
		return new Result(null);
	}

}
