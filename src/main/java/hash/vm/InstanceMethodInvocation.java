package hash.vm;


public class InstanceMethodInvocation extends Invocation {

	private Expression target;
	private java.lang.reflect.Method method;

	public Expression getTarget() {
		return target;
	}

	public void setTarget(Expression target) {
		this.target = target;
	}

	protected java.lang.reflect.Method getMethod() {
		return method;
	}
	
	public void setMethod(java.lang.reflect.Method method) {
		this.method = method;
	}

	@Override
	public Class getDataType() {
		return method.getReturnType();
	}
}
