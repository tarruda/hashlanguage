package hash.runtime.mixins;

import hash.runtime.functions.BuiltinMethod;
import hash.util.Check;
import hash.util.Constants;
import hash.util.Err;

import java.lang.reflect.Array;

@SuppressWarnings("serial")
public class ArrayMixin extends Mixin {

	public static final ArrayMixin INSTANCE = new ArrayMixin();

	private ArrayMixin() {
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
	}
}
