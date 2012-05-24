package hash.jvm;

import java.lang.reflect.Method;

public class StaticMethodInvocation extends Invocation {

	
	private Method method;	

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
