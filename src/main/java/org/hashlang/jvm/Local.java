package org.hashlang.jvm;

public class Local extends Expression {

	private int index;
	private Class type;

	@Override
	public Class getDataType() {
		return getType();
	}

	public Class getType() {
		return type;
	}

	public void setType(Class type) {
		this.type = type;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
