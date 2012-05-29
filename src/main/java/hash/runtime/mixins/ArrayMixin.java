package hash.runtime.mixins;

import hash.runtime.AppRuntime;
import hash.runtime.functions.BuiltinMethod;
import hash.runtime.operations.ArrayOperations;
import hash.util.Check;
import hash.util.Constants;
import hash.util.Err;

import java.lang.reflect.Array;

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
