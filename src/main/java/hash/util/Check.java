package hash.util;

import hash.runtime.exceptions.IllegalArgCountException;

public class Check {
	public static void numberOfArgs(Object[] args, int expected) {
		if (args.length != expected)
			throw new IllegalArgCountException();
	}

	public static boolean integerResult(Object arg1, Object arg2) {
		return (arg1.getClass() == Long.class || arg1.getClass() == Integer.class)
				&& (arg2.getClass() == Long.class || arg2.getClass() == Integer.class);
	}
}
