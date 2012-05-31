package org.hashlang.runtime.mixins;


import java.lang.reflect.Array;

import org.hashlang.runtime.AppRuntime;
import org.hashlang.runtime.functions.BuiltinMethod;
import org.hashlang.runtime.operations.ArrayOperations;
import org.hashlang.util.Check;
import org.hashlang.util.Constants;
import org.hashlang.util.Err;

@SuppressWarnings("serial")
public class ArrayMixin extends Mixin {

	public ArrayMixin(AppRuntime r) {
		super(r);
		installMethod(new BuiltinMethod(Constants.GET_INDEX) {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object array = args[0];
				Object key = args[1];
				if (key.getClass() != Integer.class)
					throw Err.illegalArg(name,
							Integer.class.getCanonicalName(), 1);
				return Array.get(array, (Integer) key);
			}
		});
		installMethod(new BuiltinMethod(Constants.SET_INDEX) {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 3);
				Object array = args[0];
				Object key = args[1];
				Object value = args[2];
				if (!(key instanceof Integer))
					throw Err.illegalArg(name,
							Integer.class.getCanonicalName(), 1);
				Array.set(array, (Integer) key, value);
				return null;
			}
		});
		installMethod(new BuiltinMethod(Constants.GET_SLICE) {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 4);
				Object array = args[0];
				Object lowerBound = args[1];
				Object upperBound = args[2];
				Object step = args[3];
				int start = 0;
				int end = Array.getLength(array) - 1;
				int inc = 1;
				if (lowerBound instanceof Integer)
					start = (Integer) lowerBound;
				if (upperBound instanceof Integer)
					end = (Integer) upperBound;
				if (step instanceof Integer)
					inc = (Integer) step;
				return ArrayOperations.slice(array, start, end, inc);
			}
		});
	}
}
