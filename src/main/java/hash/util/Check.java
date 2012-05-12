package hash.util;

public class Check {

	public static void numberOfArgs(Object[] args, int expected) {
		if (args.length != expected)
			throw Err.illegalArgCount();
	}

	public static void sliceBounds(int lower, int upper, int step, int size) {
		if (step < 1)
			throw Err.illegalArg("Step must be equal or greater than 1");
		if (lower == upper)
			throw Err.illegalArg("Lower and upper bounds must be different");
		int maxIndex = size - 1;
		int minIndex = -size;
		if (lower < minIndex || lower > maxIndex || upper < minIndex
				|| upper > maxIndex)
			throw Err.outOfBounds();
	}
}
