package hash.vm;

import java.lang.reflect.Constructor;

public class ConstructorInvocation extends Invocation {

	private Constructor constructor;

	public Constructor getConstructor() {
		return constructor;
	}

	public void setConstructor(Constructor constructor) {
		this.constructor = constructor;
	}

	@Override
	public Class getDataType() {
		return constructor.getDeclaringClass();
	}
}
