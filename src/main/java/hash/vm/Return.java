package hash.vm;

public class Return extends Statement {
	private Expression object;

	public Expression getObject() {
		return object;
	}

	public void setObject(Expression object) {
		this.object = object;
	}
}
