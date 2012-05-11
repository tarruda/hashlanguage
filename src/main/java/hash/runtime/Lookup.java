package hash.runtime;

import hash.lang.Function;
import hash.runtime.bridge.HashToJava;
import hash.runtime.exceptions.AttributeNotFoundException;
import hash.runtime.exceptions.HashException;
import hash.util.Constants;

import java.util.Map;

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
			throw new HashException(String.format(
					"Attribute '%s' is not a function", methodKey));
		Object[] methodArgs = new Object[args.length + 1];
		methodArgs[0] = target;
		for (int i = 0; i < args.length; i++)
			methodArgs[i + 1] = args[i];
		return ((Function) f).invoke(methodArgs);
	}

	public static Object invokeFunction(Object f, Object... args) {
		if (!(f instanceof Function))
			throw new HashException(String.format(
					"Object '%s' is not a function", f));
		return ((Function) f).invoke(args);
	}

	/*
	 * Objects in the Hash language can override the default behavior of the
	 * attribute/item accessors.
	 * 
	 * The operations 'get', 'set', 'has' and 'del' are done through the
	 * following which will directly search in the object 'class' hierarchy for
	 * the corresponding accessor.
	 */
	public static Object getAttribute(Object target, Object key) {
		return getAcessor(target, Constants.GET_ATTRIBUTE).invoke(target, key);
	}

	public static Object setAttribute(Object target, Object key, Object value) {
		return getAcessor(target, Constants.SET_ATTRIBUTE).invoke(target, key,
				value);
	}

	public static Object delAttribute(Object target, Object key) {
		return getAcessor(target, Constants.DEL_ATTRIBUTE).invoke(target, key);
	}

	public static Object getItem(Object target, Object key) {
		return getAcessor(target, Constants.GET_ITEM).invoke(target, key);
	}

	public static Object setItem(Object target, Object key, Object value) {
		return getAcessor(target, Constants.SET_ITEM)
				.invoke(target, key, value);
	}

	public static Object hasItem(Object target, Object key) {
		return getAcessor(target, Constants.HAS_ITEM).invoke(target, key);
	}

	public static Object delItem(Object target, Object key) {
		return getAcessor(target, Constants.DEL_ITEM).invoke(target, key);
	}

	public static Function getAcessor(Object obj, Object key) {
		Map cls = HashToJava.getClass(obj);
		Object rv = null;
		while (rv == null && cls != null) {
			rv = cls.get(key);
			cls = HashToJava.getSuperclass(cls);
		}
		if (rv instanceof Function)
			return (Function) rv;
		throw new HashException(String.format(
				"This object is missing the '%s' accessor", key));
	}
}
