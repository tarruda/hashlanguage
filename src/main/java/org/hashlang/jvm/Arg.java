package org.hashlang.jvm;

public class Arg extends Expression {
	private int index;

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	@Override
	public Class getDataType() {
		return Object.class;
	}
}
