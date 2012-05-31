package org.hashlang.runtime.operations;


public class StringOperations {

	public static String multiplication(String s, int n) {
		StringBuilder rv = new StringBuilder();
		for (int i = 0; i < n; i++)
			rv.append(s);
		return rv.toString();
	}

	public static String slice(String target, int lowerBound, int upperBound,
			int step) {
		int size = target.length();
		int[] indexes = Common.sliceIndexes(lowerBound, upperBound, step, size);
		StringBuilder rv = new StringBuilder(indexes.length);
		for (int i = 0; i < indexes.length; i++) 
			rv.appendCodePoint(target.charAt(indexes[i]));
		return rv.toString();
	}

}
