package hash.vm;


public class AreEqual extends BranchCondition {

	private Expression left;

	private Expression right;

	public Expression getLeft() {
		return left;
	}
	
	public void setLeft(Expression left) {
		this.left = left;
	}

	public Expression getRight() {
		return right;
	}
	
	public void setRight(Expression right) {
		this.right = right;
	}
}
