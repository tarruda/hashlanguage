package hash.parsing.visitors;

import hash.parsing.tree.HashNode;
import hash.parsing.visitors.simplevm.Result;

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
	protected HashNode visitInteger(HashNode node) {
		String txt = node.getText();
		Long val = Long.parseLong(txt, 16);
		int iVal = val.intValue();
		if (val.longValue() == iVal)
			return new Result(iVal);
		return new Result(val);
	}

	@Override
	protected HashNode visitFloat(HashNode node) {
		String txt = node.getText();
		Double val = Double.parseDouble(txt);
		return new Result(val);
	}

	@Override
	protected HashNode visitString(HashNode node) {
		return new Result(node.getText());
	}

	@Override
	protected HashNode visitBoolean(HashNode node) {
		String txt = node.getText();
		return new Result(Boolean.parseBoolean(txt));
	}

	@Override
	protected HashNode visitNull(HashNode node) {
		return new Result(null);
	}

}
