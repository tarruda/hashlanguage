package org.hashlang.jvm;

import java.util.ArrayList;
import java.util.List;

public abstract class Invocation extends Expression {
	private List<Expression> args = new ArrayList<Expression>();

	public void addArgument(Expression arg) {
		args.add(arg);
	}

	public List<Expression> getArgs() {
		return args;
	}

	public Expression getArg(int i) {
		return args.get(i);
	}
}
