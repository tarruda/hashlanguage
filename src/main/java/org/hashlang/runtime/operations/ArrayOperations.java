package org.hashlang.runtime.operations;

import java.lang.reflect.Array;

public class ArrayOperations {

	public static Object slice(Object target, int lowerBound, int upperBound,
			int step) {
		int size = Array.getLength(target);
		int[] indexes = Common.sliceIndexes(lowerBound, upperBound, step, size);
		Object rv = Array.newInstance(target.getClass().getComponentType(),
				indexes.length);
		for (int i = 0; i < indexes.length; i++)
			Array.set(rv, i, Array.get(target, indexes[i]));
		return rv;
	}
}
