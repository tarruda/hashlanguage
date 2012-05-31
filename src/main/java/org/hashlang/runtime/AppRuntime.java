package org.hashlang.runtime;


import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.hashlang.runtime.functions.BinaryOperator;
import org.hashlang.runtime.functions.UnaryOperator;
import org.hashlang.runtime.mixins.ArrayMixin;
import org.hashlang.runtime.mixins.BooleanMixin;
import org.hashlang.runtime.mixins.CharacterMixin;
import org.hashlang.runtime.mixins.FloatMixin;
import org.hashlang.runtime.mixins.IntegerMixin;
import org.hashlang.runtime.mixins.ListMixin;
import org.hashlang.runtime.mixins.MapMixin;
import org.hashlang.runtime.mixins.NumberMixin;
import org.hashlang.runtime.mixins.ObjectMixin;
import org.hashlang.runtime.mixins.RegexMixin;
import org.hashlang.runtime.mixins.StringMixin;
import org.hashlang.util.Check;
import org.hashlang.util.Constants;
import org.hashlang.util.Err;

public class AppRuntime {

	private final HashMap<Class<?>, HashObject> classMap;
	private final HashMap<Class<?>, Map[]> classMixins;
	private final ArrayMixin arrayMixin;
	private InputStream stdin;
	private PrintStream stdout;
	private PrintStream stderr;
	private Map imports;
	private Function classHandler;
	private Function importHandler;
	private List<String> searchPaths;
	private Module main;

	public AppRuntime() {
		this(System.in, System.out, System.err);
	}

	public AppRuntime(InputStream stdin, PrintStream stdout, PrintStream stderr) {
		this(stdin, stdout, stderr, Factory.createMap(), null, null);
		final AppRuntime runtime = this;
		this.classHandler = new Function() {
			public Object invoke(Object... args) throws Throwable {
				Check.numberOfArgs(args, 3);
				return runtime.createClass((Map) args[1], (HashObject) args[2]);
			}
		};
		this.importHandler = new Function() {
			public Object invoke(Object... args) throws Throwable {
				Check.numberOfArgs(args, 2);
				String name = (String) args[1];
				if (!runtime.imports.containsKey(name)) {
					synchronized (imports) {
						if (!runtime.imports.containsKey(name)) {
							try {
								runtime.hashImport(name);
							} catch (Exception ex) {
								runtime.javaImport(name);
							}
						}
					}
				}
				return runtime.imports.get(name);
			}
		};
	}

	public AppRuntime(InputStream stdin, PrintStream stdout,
			PrintStream stderr, Map imports, Function classHandler,
			Function importHandler) {
		this.stdin = stdin;
		this.stdout = stdout;
		this.stderr = stderr;
		this.imports = imports;
		this.classHandler = classHandler;
		this.importHandler = importHandler;
		this.searchPaths = new ArrayList<String>();
		arrayMixin = new ArrayMixin(this);
		classMap = new HashMap<Class<?>, HashObject>();
		IntegerMixin integerMixin = new IntegerMixin(this);
		FloatMixin floatMixin = new FloatMixin(this);
		classMixins = new HashMap<Class<?>, Map[]>();
		classMixins.put(Object.class, new Map[] { new ObjectMixin(this) });
		classMixins.put(Boolean.class, new Map[] { new BooleanMixin(this) });
		classMixins.put(Number.class, new Map[] { new NumberMixin(this) });
		classMixins.put(Character.class, new Map[] { integerMixin,
				new CharacterMixin(this) });
		classMixins.put(Byte.class, new Map[] { integerMixin });
		classMixins.put(Short.class, new Map[] { integerMixin });
		classMixins.put(Integer.class, new Map[] { integerMixin });
		classMixins.put(Long.class, new Map[] { integerMixin });
		classMixins.put(Float.class, new Map[] { floatMixin });
		classMixins.put(Double.class, new Map[] { floatMixin });
		classMixins.put(String.class, new Map[] { new StringMixin(this) });
		classMixins.put(List.class, new Map[] { new ListMixin(this) });
		classMixins.put(Map.class, new Map[] { new MapMixin(this) });
		classMixins.put(Pattern.class, new Map[] { new RegexMixin(this) });
		cloneWrapper(HashObject.class);
	}

	private void hashImport(String name) {
		throw Err.notImplemented();
	}

	private void javaImport(String name) {
		Class klass = null;
		try {
			klass = Class.forName(name);
		} catch (ClassNotFoundException e) {
			try {
				String fieldName = name.substring(name.lastIndexOf('.') + 1);
				try {
					klass = Class.forName(name.substring(0,
							name.lastIndexOf('.')));
					try {
						Field f = klass.getField(fieldName);
						if (Modifier.isStatic(f.getModifiers())) {
							Object v = f.get(null);
							imports.put(name, v);
							return;
						}
					} catch (NoSuchFieldException ex) {
						throw Err.importError(name, ex);
					}

				} catch (Exception ex) {
					throw Err.importError(name, ex);
				}
			} catch (IndexOutOfBoundsException ex) {
				throw Err.importError(name, ex);
			}
		}
		HashObject kls = (HashObject) getWrapperFor(klass);
		imports.put(name, kls);
	}

	public boolean isInstance(Object obj, Object klass) {
		if (!(klass instanceof HashObject))
			return false;
		HashObject kls = getIsa(obj);
		while (kls != null) {
			if (kls.equals(klass))
				return true;
			kls = kls.getIsa();
		}
		return false;
	}

	private void mergeMixins(Class klass, HashObject wrapper) {
		Class<?>[] interfaces = klass.getInterfaces();
		for (Class<?> i : interfaces) {
			Map[] interfaceMixins = classMixins.get(i);
			if (interfaceMixins != null)
				for (Map mixin : interfaceMixins)
					for (Object key : mixin.keySet())
						wrapper.put(key, mixin.get(key));
		}
		Map[] mixins = classMixins.get(klass);
		if (mixins != null)
			for (Map mixin : mixins)
				for (Object key : mixin.keySet())
					wrapper.put(key, mixin.get(key));
		// If this is an array class, manually merge the array mixin
		if (klass.isArray())
			for (Object key : arrayMixin.keySet())
				wrapper.put(key, arrayMixin.get(key));
	}

	private void cloneWrapper(Class<?> klass) {
		HashObject wrapper = (HashObject) JvmBridge.INSTANCE.getWrapperFor(
				klass).clone();
		mergeMixins(klass, wrapper);

		// Map the class and its superclasses
		HashObject c = wrapper.getIsa();
		Class k = klass.getSuperclass();
		while (c != null) {
			mergeMixins(k, c);
			classMap.put(k, c);
			k = k.getSuperclass();
			c = c.getIsa();
		}
		classMap.put(klass, wrapper);
	}

	protected HashObject getWrapperFor(Class<?> klass) {
		if (!classMap.containsKey(klass))
			synchronized (classMap) {
				if (!classMap.containsKey(klass)) {
					cloneWrapper(klass);
				}
			}
		return classMap.get(klass);
	}

	protected HashObject getIsa(Object object) {
		if (object.getClass() == HashObject.class)
			return ((HashObject) object).getIsa();
		return getWrapperFor(object.getClass());
	}

	public HashObject newObj(HashObject klass) {
		HashObject rv = new HashObject();
		rv.setIsa(klass);
		return rv;
	}

	public HashObject createClass(Map map, HashObject superClass) {
		HashObject rv = new HashObject(map);
		if (superClass != null)
			rv.setIsa(superClass);
		else
			rv.setIsa(getWrapperFor(HashObject.class));
		rv.put(Constants.CONSTRUCTOR, new Function() {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 1);
				return newObj((HashObject) args[0]);
			}
		});
		return rv;
	}

	public Object invokeFunction(Object f, Object... args) throws Throwable {
		Object[] fArgs = new Object[args.length + 1];
		fArgs[0] = null;
		for (int i = 0; i < args.length; i++)
			fArgs[i + 1] = args[i];
		if (!(f instanceof Function))
			Err.illegalArg(String.format("Object '%s' is not a function", f));
		return ((Function) f).invoke(fArgs);
	}

	public Object invokeBinaryOperator(String operator, Object leftOperand,
			Object rightOperand) throws Throwable {
		return invokeSpecialMethod(leftOperand,
				BinaryOperator.getSlotName(operator), rightOperand);
	}

	public Object invokeUnaryOperator(String operator, Object operand)
			throws Throwable {
		return invokeSpecialMethod(operand, UnaryOperator.getSlotName(operator));
	}

	public Object invokeNormalMethod(Object target, Object methodKey,
			Object... args) throws Throwable {
		Object f = getAttribute(target, methodKey);
		if (!(f instanceof Function))
			throw Err.attributeNotFunction(methodKey);
		return invokeMethod((Function) f, target, methodKey, args);
	}

	/**
	 * Invokes a method ignoring the 'getAttr/getItem' accessors.
	 * 
	 * @throws Throwable
	 */
	public Object invokeSpecialMethod(Object target, Object methodKey,
			Object... args) throws Throwable {
		Function f = getSpecialMethod(target, methodKey);
		return invokeMethod(f, target, methodKey, args);
	}

	private Object invokeMethod(Function f, Object target, Object methodKey,
			Object... args) throws Throwable {
		Object[] methodArgs = new Object[args.length + 1];
		methodArgs[0] = target;
		for (int i = 0; i < args.length; i++)
			methodArgs[i + 1] = args[i];
		return f.invoke(methodArgs);
	}

	public Object getAttribute(Object target, Object key) throws Throwable {
		return getSpecialMethod(target, Constants.GET_ATTRIBUTE).invoke(target,
				key);
	}

	public Object setAttribute(Object target, Object key, Object value)
			throws Throwable {
		return getSpecialMethod(target, Constants.SET_ATTRIBUTE).invoke(target,
				key, value);
	}

	public Object getIndex(Object target, Object key) throws Throwable {
		return getSpecialMethod(target, Constants.GET_INDEX)
				.invoke(target, key);
	}

	public Object setIndex(Object target, Object key, Object value)
			throws Throwable {
		return getSpecialMethod(target, Constants.SET_INDEX).invoke(target,
				key, value);
	}

	public Object getSlice(Object target, Object lowerBound, Object upperBound,
			Object step) throws Throwable {
		return getSpecialMethod(target, Constants.GET_SLICE).invoke(target,
				lowerBound, upperBound, step);
	}

	/**
	 * Looks for an real method in an object class hierarchy(instead of first
	 * invoking its 'get' accessor which may be overriden.
	 */
	public Function getSpecialMethod(Object obj, Object key) {
		Object rv = lookup(obj, key);
		if (rv instanceof Function)
			return (Function) rv;
		throw Err.attributeNotFunction(key);
	}

	public Object lookup(Object obj, Object key) {
		Object rv = null;
		if (obj instanceof Map)
			rv = ((Map) obj).get(key);
		if (rv != null)
			return rv;
		HashObject cls = getIsa(obj);
		while (rv == null && cls != null) {
			rv = cls.get(key);
			cls = cls.getIsa();
		}
		if (rv == null)
			throw Err.attributeNotDefined(key);
		return rv;
	}

	public static Throwable throwableObj(Object throwable) {
		if (throwable instanceof Throwable)
			return (Throwable) throwable;
		else
			return new RuntimeException(throwable.toString());
	}

	public Iterator getIterator(Object obj) {
		if (obj instanceof Iterable)
			return ((Iterable) obj).iterator();
		else if (obj instanceof Continuation)
			return new ContinuationIterator((Continuation) obj);
		else if (obj instanceof Map)
			return new MapIterator((Map) obj);
		throw Err
				.illegalArg("For loop cannot get an iterator from this object");
	}

	public Object iteratorNext(Object obj) {
		return ((Iterator) obj).next();
	}

	public boolean iteratorHasNext(Object obj) {
		return ((Iterator) obj).hasNext();
	}

	public Context getContext(int level, Context current) {
		Context c = current;
		while (level > 0 && c.getParent() != null) {
			c = c.getParent();
			level--;
		}
		return c;
	}

	public Object jumpTo(Object obj, Object arg) throws Throwable {
		if (obj instanceof Continuation)
			return new Jump((Continuation) obj, arg);
		throw Err.illegalArg("Not a continuation");

	}

	public Map getImports() {
		return imports;
	}

	public Module getMain() {
		if (main == null) {
			main = Factory.createModule();
			main.put(Constants.CLASS, getClassHandler());
			main.put(Constants.IMPORT, getImportHandler());
			main.put("out", stdout);
			main.put("err", stderr);
		}
		return main;
	}

	public InputStream getStdin() {
		return stdin;
	}

	public PrintStream getStdout() {
		return stdout;
	}

	public PrintStream getStderr() {
		return stderr;
	}

	public void addSearchPath(String path) {
		searchPaths.add(path);
	}

	public Function getClassHandler() {
		return classHandler;
	}

	public Function getImportHandler() {
		return importHandler;
	}

}
