package hash.runtime.functions;

import hash.runtime.Function;

public abstract class JavaMethod implements Function {

	private String methodName;
	private String className;
	private String methodType;

	public JavaMethod(String methodName, String className, boolean isStatic) {
		this.methodName = methodName;
		this.className = className;
		this.methodType = isStatic ? "static" : "instance";
	}

	@Override
	public String toString() {
		return String.format("%s method '%s.%s'", methodType, className,
				methodName);
	}
}
