package hash.runtime.operations;

import hash.util.Err;

import java.util.ArrayList;

public class Common {

	public static int calculateAbsoluteIndex(int index, int size) {
		int rv = index;
		if (index < 0)
			rv = size + index;
		return rv;
	}

	public static int[] sliceIndexes(int lowerBound, int upperBound, int step,
			int originalSize) {
		if (step < 1)
			throw Err.illegalArg("Step must be equal or greater than 1");
		if (lowerBound == upperBound)
			throw Err.illegalArg("Lower and upper bounds must be different");
		int maxIndex = originalSize - 1;
		int minIndex = -originalSize;
		if (lowerBound < minIndex || lowerBound > maxIndex
				|| upperBound < minIndex || upperBound > maxIndex)
			throw Err.outOfBounds();
		int absoluteLowerBound = calculateAbsoluteIndex(lowerBound,
				originalSize);
		int absoluteUpperBound = calculateAbsoluteIndex(upperBound,
				originalSize);
		if (absoluteUpperBound < absoluteLowerBound)
			step = -step;
		ArrayList<Integer> indexes = new ArrayList<Integer>(originalSize);
		if (step > 0)
			for (int i = absoluteLowerBound; i <= absoluteUpperBound; i = i
					+ step)
				indexes.add(i);
		else
			for (int i = absoluteLowerBound; i >= absoluteUpperBound; i = i
					+ step)
				indexes.add(i);
		int[] rv = new int[indexes.size()];
		for (int i = 0; i < rv.length; i++)
			rv[i] = indexes.get(i);
		return rv;
	}

}
