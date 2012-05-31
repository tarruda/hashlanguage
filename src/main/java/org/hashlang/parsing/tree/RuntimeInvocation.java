package org.hashlang.parsing.tree;

import static org.hashlang.parsing.HashParser.RUNTIME_INVOCATION;

public class RuntimeInvocation extends CommonHashNode {

	public static final int GET_ITERATOR = 1;
	public static final int ITERATOR_HASNEXT = 2;
	public static final int ITERATOR_NEXT = 3;

	private int runtimeMethod;
	private HashNode[] args;

	public RuntimeInvocation(int runtimeMethod, HashNode... args) {
		super(RUNTIME_INVOCATION);
		this.runtimeMethod = runtimeMethod;
		this.args = args;
	}

	public int getRuntimeMethod() {
		return runtimeMethod;
	}

	public HashNode[] getArgs() {
		return args;
	}
}
