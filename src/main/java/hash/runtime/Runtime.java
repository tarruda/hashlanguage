package hash.runtime;

import hash.lang.Function;
import hash.runtime.bridge.HashToJava;
import hash.runtime.exceptions.AttributeNotFoundException;
import hash.runtime.exceptions.HashException;
import hash.runtime.functions.BinaryOperator;
import hash.runtime.functions.UnaryOperator;
import hash.util.Constants;

import java.util.Map;

public class Runtime {

	public static Object invokeFunction(Object f, Object... args) {
		if (!(f instanceof Function))
			throw new HashException(String.format(
					"Object '%s' is not a function", f));
		return ((Function) f).invoke(args);
	}

	public static Object invokeBinaryOperator(String operator, Object lhs,
			Object rhs) {
		return invokeRealMethod(lhs, BinaryOperator.getSlotName(operator), rhs);
	}

	public static Object invokeUnaryOperator(String operator, Object operand) {
		return invokeRealMethod(operand, UnaryOperator.getSlotName(operator));
	}

	public static Object invokeMethod(Object target, Object methodKey,
			Object... args) {
		Object f = getAttribute(target, methodKey);
		return invokeMethodCore(f, target, methodKey, args);
	}

	/**
	 * Invokes a method ignoring the 'getAttr/getItem' accessors.
	 */
	public static Object invokeRealMethod(Object target, Object methodKey,
			Object... args) {
		Object f = getMethod(target, methodKey);
		return invokeMethodCore(f, target, methodKey, args);
	}

	private static Object invokeMethodCore(Object f, Object target,
			Object methodKey, Object... args) {
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

	public static Object getAttribute(Object target, Object key) {
		return getMethod(target, Constants.GET_ATTRIBUTE).invoke(target, key);
	}

	public static Object setAttribute(Object target, Object key, Object value) {
		return getMethod(target, Constants.SET_ATTRIBUTE).invoke(target, key,
				value);
	}

	public static Object delAttribute(Object target, Object key) {
		return getMethod(target, Constants.DEL_ATTRIBUTE).invoke(target, key);
	}

	public static Object getIndex(Object target, Object key) {
		return getMethod(target, Constants.GET_INDEX).invoke(target, key);
	}

	public static Object setIndex(Object target, Object key, Object value) {
		return getMethod(target, Constants.SET_INDEX)
				.invoke(target, key, value);
	}

	public static Object hasIndex(Object target, Object key) {
		return getMethod(target, Constants.HAS_INDEX).invoke(target, key);
	}

	public static Object delIndex(Object target, Object key) {
		return getMethod(target, Constants.DEL_INDEX).invoke(target, key);
	}

	public static Object getSlice(Object target, Object lowerBound,
			Object upperBound, Object step) {
		return getMethod(target, Constants.GET_SLICE).invoke(target,
				lowerBound, upperBound, step);
	}

	/**
	 * Looks for an real method in an object class hierarchy(instead of first
	 * invoking its 'get' accessor which may be overriden. *
	 */
	public static Function getMethod(Object obj, Object key) {
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
