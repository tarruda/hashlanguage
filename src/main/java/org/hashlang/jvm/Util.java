package org.hashlang.jvm;

public class Util {
	public static Class getBoxedClass(Class rv) {
		if (rv == Boolean.TYPE)
			rv = Boolean.class;
		else if (rv == Character.TYPE)
			rv = Character.class;
		else if (rv == Byte.TYPE)
			rv = Byte.class;
		else if (rv == Short.TYPE)
			rv = Short.class;
		else if (rv == Integer.TYPE)
			rv = Integer.class;
		else if (rv == Long.TYPE)
			rv = Long.class;
		else if (rv == Float.TYPE)
			rv = Float.class;
		else if (rv == Double.TYPE)
			rv = Double.class;
		return rv;
	}
}
