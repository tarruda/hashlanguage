package hash.runtime;

import hash.lang.Function;
import hash.lang.Hash;
import hash.runtime.exceptions.AttributeNotFoundException;
import hash.runtime.exceptions.HashException;

public class Lookup {

	public static Object invokeBinaryOperator(String operator, Object lhs,
			Object rhs) {
		return invokeMethod(lhs, operator + "##", rhs);
	}

	public static Object invokeMethod(Object target, String name,
			Object... args) {
		Object f = getAttribute(target, name);
		if (f == null)
			throw new AttributeNotFoundException(name);
		if (!(f instanceof Function))
			throw new HashException(String.format("'%s' is not a function",
					name));
		Object[] methodArgs = new Object[args.length + 1];
		methodArgs[0] = target;
		for (int i = 0; i < args.length; i++)
			methodArgs[i + 1] = args[i];
		return ((Function) f).invoke(methodArgs);
	}

	public static Object getAttribute(Object target, String name) {
		Hash cls = JavaBridge.getClassFor(target);
		Object rv = null;
		while (rv == null && cls != null) {
			rv = cls.get(name);
			cls = JavaBridge.getSuperClassFor(cls);
		}
		return rv;
	}
}
