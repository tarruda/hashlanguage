package hash.runtime;

import hash.lang.Function;
import hash.lang.Hash;
import hash.runtime.bridge.HashToJava;
import hash.runtime.exceptions.AttributeNotFoundException;
import hash.runtime.exceptions.HashException;

public class Lookup {

	public static Object invokeBinaryOperator(String operator, Object lhs,
			Object rhs) {
		return invokeMethod(lhs, operator + "##", rhs);
	}

	public static Object invokeUnaryOperator(String operator, Object operand) {
		return invokeMethod(operand, operator + "#");
	}

	public static Object invokeMethod(Object target, Object methodKey,
			Object... args) {
		Object f = getAttribute(target, methodKey);
		if (f == null)
			throw new AttributeNotFoundException(methodKey.toString());
		if (!(f instanceof Function))
			throw new HashException(String.format("Attribute '%s' is not a function",
					methodKey));
		Object[] methodArgs = new Object[args.length + 1];
		methodArgs[0] = target;
		for (int i = 0; i < args.length; i++)
			methodArgs[i + 1] = args[i];
		return ((Function) f).invoke(methodArgs);
	}

	public static Object invokeFunction(Object f, Object... args) {
		if (!(f instanceof Function))
			throw new HashException("Object is not a function");
		return ((Function) f).invoke(args);
	}

	public static Object getAttribute(Object target, Object key) {
		Hash cls = HashToJava.getClass(target);
		Object rv = null;
		while (rv == null && cls != null) {
			rv = cls.get(key);
			cls = HashToJava.getSuperclass(cls);
		}
		return rv;
	}

}
