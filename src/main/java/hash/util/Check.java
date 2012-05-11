package hash.util;

public class Check {

	public static void numberOfArgs(Object[] args, int expected) {
		if (args.length != expected)
			throw Err.illegalArgCount();
	}
}
