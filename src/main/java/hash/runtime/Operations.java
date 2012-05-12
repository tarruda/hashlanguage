package hash.runtime;

import hash.lang.Factory;
import hash.util.Err;

import java.util.List;

public class Operations {

	public static String multiplicateString(String s, int n) {
		StringBuilder rv = new StringBuilder();
		for (int i = 0; i < n; i++)
			rv.append(s);
		return rv.toString();
	}

	public static List listSlice(List target, int lowerBound, int upperBound,
			int step) {
		if (step < 1)
			throw Err.illegalArg("Step must be equal or greater than 1");
		if (lowerBound == upperBound)
			throw Err.illegalArg("Lower and upper bounds must be different");
		int listSize = target.size();
		int maxIndex = listSize - 1;
		int minIndex = -listSize;
		if (lowerBound < minIndex || lowerBound > maxIndex
				|| upperBound < minIndex || upperBound > maxIndex)
			throw Err.outOfBounds();
		int actualLowerBound = lowerBound % listSize;
		int actualUpperBound = upperBound % listSize;
		if (actualUpperBound < actualLowerBound)
			step = -step;
		List rv = Factory.createList();
		if (step > 0)
			for (int i = actualLowerBound; i <= actualUpperBound; i = i + step)
				rv.add(target.get(i));
		else
			for (int i = actualLowerBound; i >= actualUpperBound; i = i + step)
				rv.add(target.get(i));
		return rv;
	}
}
