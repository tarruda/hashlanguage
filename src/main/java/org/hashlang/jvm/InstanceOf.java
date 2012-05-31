package org.hashlang.jvm;


public class InstanceOf extends BranchCondition {
	private Expression object;
	private Class<?> klass;

	public Expression getObject() {
		return object;
	}

	public void setObject(Expression object) {
		this.object = object;
	}

	public Class getBoxedClass() {
		return klass;
	}

	public void setBoxedClass(Class<?> klass) {
		this.klass = Util.getBoxedClass(klass);
	}
}
