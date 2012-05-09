package hash.runtime.bridge;

import hash.lang.Function;

public abstract class JavaMethodWrapper implements Function {

	private String methodName;
	private String className;
	private String methodType;

	public Object stub(char b0, boolean b1, byte b2, short b3, int b4, long b5,
			float b6, double b7) {
		return b7;
	}

	public JavaMethodWrapper(String methodName, String className,
			boolean isStatic) {
		this.methodName = methodName;
		this.className = className;
		this.methodType = isStatic ? "static" : "instance";
	}

	@Override
	public String toString() {
		return String.format("%s method '%s' of class '%s'", methodType,
				methodName, className);
	}
}
