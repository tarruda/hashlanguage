package org.hashlang.runtime.mixins;


import java.util.List;

import org.hashlang.runtime.AppRuntime;
import org.hashlang.runtime.functions.BinaryOperator;
import org.hashlang.runtime.functions.BuiltinMethod;
import org.hashlang.runtime.operations.Common;
import org.hashlang.runtime.operations.ListOperations;
import org.hashlang.util.Check;
import org.hashlang.util.Constants;
import org.hashlang.util.Err;

@SuppressWarnings("serial")
public class ListMixin extends Mixin {

	public ListMixin(AppRuntime r) {
		super(r);
		installMethod(new BinaryOperator(Constants.CONTAINS) {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				List self = (List) args[0];
				Object other = args[1];
				return self.contains(other);				
			}
		});
		installMethod(new BuiltinMethod(Constants.BOOLEAN_VALUE) {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 1);
				return ((List) args[0]).size() != 0;
			}
		});
		installMethod(new BuiltinMethod(Constants.GET_INDEX) {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				List list = (List) args[0];
				if (args[1] == null)
					throw Err.nullIndex();
				int idx = (Integer) args[1];
				return list
						.get(Common.calculateAbsoluteIndex(idx, list.size()));
			}
		});
		installMethod(new BuiltinMethod(Constants.SET_INDEX) {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 3);
				List list = (List) args[0];
				if (args[1] == null)
					throw Err.nullIndex();
				int idx = (Integer) args[1];
				Object value = args[2];
				return list.set(
						Common.calculateAbsoluteIndex(idx, list.size()), value);
			}
		});
		installMethod(new BuiltinMethod(Constants.DEL_INDEX) {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				List list = (List) args[0];
				if (args[1] == null)
					throw Err.nullIndex();
				int idx = (Integer) args[1];
				return list.remove(Common.calculateAbsoluteIndex(idx,
						list.size()));
			}
		});
		installMethod(new BuiltinMethod(Constants.GET_SLICE) {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 4);
				List list = (List) args[0];
				Object lowerBound = args[1];
				Object upperBound = args[2];
				Object step = args[3];
				int start = 0;
				int end = list.size() - 1;
				int inc = 1;
				if (lowerBound instanceof Integer)
					start = (Integer) lowerBound;
				if (upperBound instanceof Integer)
					end = (Integer) upperBound;
				if (step instanceof Integer)
					inc = (Integer) step;
				return ListOperations.slice(list, start, end, inc);
			}
		});
	}
}
