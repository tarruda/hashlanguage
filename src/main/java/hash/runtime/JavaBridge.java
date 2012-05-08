package hash.runtime;

import hash.lang.Hash;
import hash.runtime.classes.NumberMixin;

import java.util.HashMap;

public class JavaBridge {

	private static final HashMap<Class<?>, Hash> classMap;

	static {
		classMap = new HashMap<Class<?>, Hash>();
		classMap.put(Long.class, new NumberMixin());
		classMap.put(Integer.class, new NumberMixin());
		classMap.put(Double.class, new NumberMixin());
		classMap.put(Float.class, new NumberMixin());
	}

	public static Hash getClassFor(Object object) {
		if (object instanceof Hash)
			return (Hash) object;
		return getSuperClassFor(object);
	}

	public static Hash getSuperClassFor(Object object) {
		if (object instanceof Hash) {
			Object rv = ((Hash) object).get("super");
			if (rv instanceof Hash)
				return (Hash) rv;
			return null;
		}
		return classMap.get(object.getClass());
	}
}
