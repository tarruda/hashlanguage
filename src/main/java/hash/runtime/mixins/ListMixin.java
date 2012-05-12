package hash.runtime.mixins;

import hash.runtime.Operations;
import hash.runtime.functions.BuiltinMethod;
import hash.util.Check;
import hash.util.Constants;
import hash.util.Err;

import java.util.List;

@SuppressWarnings("serial")
public class ListMixin extends Mixin {

	public static final ListMixin INSTANCE = new ListMixin();

	private ListMixin() {
		installMethod(new BuiltinMethod(Constants.BOOLEAN_VALUE) {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 1);
				return ((List) args[0]).size() != 0;
			}
		});
		installMethod(new BuiltinMethod(Constants.GET_INDEX) {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object list = args[0];
				Object key = args[1];
				if (key.getClass() != Integer.class)
					throw Err.illegalArg(name,
							Integer.class.getCanonicalName(), 1);
				return ((List) list).get((Integer) key);
			}
		});
		installMethod(new BuiltinMethod(Constants.SET_INDEX) {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 3);
				Object list = args[0];
				Object key = args[1];
				Object value = args[2];
				if (!(key instanceof Integer))
					throw Err.illegalArg(name,
							Integer.class.getCanonicalName(), 1);
				return ((List) list).set((Integer) key, value);
			}
		});
		installMethod(new BuiltinMethod(Constants.HAS_INDEX) {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object list = args[0];
				Object value = args[1];
				return ((List) list).contains(value);
			}
		});
		installMethod(new BuiltinMethod(Constants.DEL_INDEX) {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object list = args[0];
				Object key = args[1];
				if (key.getClass() != Integer.class)
					throw Err.illegalArg(name,
							Integer.class.getCanonicalName(), 1);
				return ((List) list).remove(((Integer) key).intValue());
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
				return Operations.listSlice(list, start, end, inc);
			}
		});
	}
}
