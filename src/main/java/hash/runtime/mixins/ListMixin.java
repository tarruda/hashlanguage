package hash.runtime.mixins;

import hash.runtime.functions.BuiltinMethod;
import hash.util.Check;
import hash.util.Constants;
import hash.util.Err;

import java.util.List;

@SuppressWarnings("serial")
public class ListMixin extends Mixin {

	public static final ListMixin INSTANCE = new ListMixin();

	private ListMixin() {
		installMethod(new BuiltinMethod(Constants.GET_ITEM) {
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
		installMethod(new BuiltinMethod(Constants.SET_ITEM) {
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

	}
}
