package hash.runtime;

import hash.lang.Function;
import hash.runtime.functions.BinaryOperator;
import hash.runtime.functions.UnaryOperator;
import hash.runtime.generators.HashAdapter;
import hash.util.Check;
import hash.util.Constants;
import hash.util.Err;

import java.util.Map;

public class Runtime {

	public static HashObject newObj(HashObject klass) {
		HashObject rv = new HashObject();
		rv.setIsa(klass);
		return rv;
	}

	public static HashObject doImport(String name) {
		Class<?> klass;
		try {
			klass = Class.forName(name);
		} catch (ClassNotFoundException e) {
			throw Err.ex(e);
		}
		return HashAdapter.getHashClass(klass);
	}

	public static HashObject createClass(Map map, HashObject superClass) {
		HashObject rv = new HashObject(map);
		if (superClass != null)
			rv.setIsa(superClass);
		else
			rv.setIsa(HashAdapter.getHashClass(HashObject.class));
		rv.put(Constants.CONSTRUCTOR, new Function() {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 1);
				return newObj((HashObject) args[0]);
			}
		});
		return rv;
	}

	public static HashObject getHashClass(Object object) {
		if (object.getClass() == HashObject.class)
			return ((HashObject) object).getIsa();
		return HashAdapter.getHashClass(object.getClass());
	}

	public static Object invokeFunction(Object f, Object... args) {
		Object[] fArgs = new Object[args.length + 1];
		fArgs[0] = null;
		for (int i = 0; i < args.length; i++)
			fArgs[i + 1] = args[i];
		if (!(f instanceof Function))
			Err.illegalArg(String.format("Object '%s' is not a function", f));
		return ((Function) f).invoke(fArgs);
	}

	public static Object invokeBinaryOperator(String operator,
			Object leftOperand, Object rightOperand) {
		return invokeSpecialMethod(leftOperand,
				BinaryOperator.getSlotName(operator), rightOperand);
	}

	public static Object invokeUnaryOperator(String operator, Object operand) {
		return invokeSpecialMethod(operand, UnaryOperator.getSlotName(operator));
	}

	public static Object invokeNormalMethod(Object target, Object methodKey,
			Object... args) {
		Object f = getAttribute(target, methodKey);
		if (!(f instanceof Function))
			throw Err.attributeNotFunction(methodKey);
		return invokeMethod((Function) f, target, methodKey, args);
	}

	/**
	 * Invokes a method ignoring the 'getAttr/getItem' accessors.
	 */
	public static Object invokeSpecialMethod(Object target, Object methodKey,
			Object... args) {
		Function f = getSpecialMethod(target, methodKey);
		return invokeMethod(f, target, methodKey, args);
	}

	private static Object invokeMethod(Function f, Object target,
			Object methodKey, Object... args) {
		Object[] methodArgs = new Object[args.length + 1];
		methodArgs[0] = target;
		for (int i = 0; i < args.length; i++)
			methodArgs[i + 1] = args[i];
		return f.invoke(methodArgs);
	}

	public static Object getAttribute(Object target, Object key) {
		return getSpecialMethod(target, Constants.GET_ATTRIBUTE).invoke(target,
				key);
	}

	public static Object setAttribute(Object target, Object key, Object value) {
		return getSpecialMethod(target, Constants.SET_ATTRIBUTE).invoke(target,
				key, value);
	}

	public static Object getIndex(Object target, Object key) {
		return getSpecialMethod(target, Constants.GET_INDEX)
				.invoke(target, key);
	}

	public static Object setIndex(Object target, Object key, Object value) {
		return getSpecialMethod(target, Constants.SET_INDEX).invoke(target,
				key, value);
	}

	public static Object getSlice(Object target, Object lowerBound,
			Object upperBound, Object step) {
		return getSpecialMethod(target, Constants.GET_SLICE).invoke(target,
				lowerBound, upperBound, step);
	}

	/**
	 * Looks for an real method in an object class hierarchy(instead of first
	 * invoking its 'get' accessor which may be overriden.
	 */
	public static Function getSpecialMethod(Object obj, Object key) {
		Object rv = lookup(obj, key);
		if (rv instanceof Function)
			return (Function) rv;
		throw Err.attributeNotFunction(key);
	}

	public static Object lookup(Object obj, Object key) {
		Object rv = null;
		if (obj instanceof Map)
			rv = ((Map) obj).get(key);
		if (rv != null)
			return rv;
		HashObject cls = getHashClass(obj);
		while (rv == null && cls != null) {
			rv = cls.get(key);
			cls = cls.getIsa();
		}
		if (rv == null)
			throw Err.attributeNotDefined(key);
		return rv;
	}

}
