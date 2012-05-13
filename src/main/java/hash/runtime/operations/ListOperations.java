package hash.runtime.operations;

import hash.runtime.Factory;

import java.util.List;

public class ListOperations {

	public static List slice(List target, int lowerBound, int upperBound,
			int step) {
		int size = target.size();
		int[] indexes = Common.sliceIndexes(lowerBound, upperBound, step, size);
		List rv = Factory.createList(indexes.length);
		for (int i = 0; i < indexes.length; i++) 
			rv.add(target.get(indexes[i]));
		return rv;
	}
	
//	public static List slice(List target, int lowerBound, int upperBound,
//			int step) {
//		int size = target.size();
//		Check.sliceBounds(lowerBound, upperBound, step, size);
//		int absoluteLowerBound = Common
//				.calculateAbsoluteIndex(lowerBound, size);
//		int absoluteUpperBound = Common
//				.calculateAbsoluteIndex(upperBound, size);
//		if (absoluteUpperBound < absoluteLowerBound)
//			step = -step;
//		List rv = Factory.createList();
//		if (step > 0)
//			for (int i = absoluteLowerBound; i <= absoluteUpperBound; i = i
//					+ step)
//				rv.add(target.get(i));
//		else
//			for (int i = absoluteLowerBound; i >= absoluteUpperBound; i = i
//					+ step)
//				rv.add(target.get(i));
//		return rv;
//	}
}
