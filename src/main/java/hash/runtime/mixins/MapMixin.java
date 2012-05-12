package hash.runtime.mixins;

import hash.runtime.Runtime;
import hash.runtime.functions.BuiltinMethod;
import hash.util.Check;
import hash.util.Constants;

import java.util.Map;

@SuppressWarnings("serial")
public class MapMixin extends Mixin {

	public static final MapMixin INSTANCE = new MapMixin();

	private MapMixin() {
		installMethod(new BuiltinMethod(Constants.SET_ATTRIBUTE) {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 3);
				return ((Map) args[0]).put(args[1], args[2]);
			}
		});
		installMethod(new BuiltinMethod(Constants.HAS_ATTRIBUTE) {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				return ((Map) args[0]).containsKey(args[1]);
			}
		});
		installMethod(new BuiltinMethod(Constants.DEL_ATTRIBUTE) {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				return ((Map) args[0]).remove(args[1]);
			}
		});
		installMethod(new BuiltinMethod(Constants.GET_INDEX) {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				return ((Map) args[0]).get(args[1]);
			}
		});
		installMethod(new BuiltinMethod(Constants.SET_INDEX) {
			public Object invoke(Object... args) {
				return Runtime.invokeMethod(args[0], Constants.SET_ATTRIBUTE);
			}
		});
		installMethod(new BuiltinMethod(Constants.HAS_INDEX) {
			public Object invoke(Object... args) {
				return Runtime.invokeMethod(args[0], Constants.HAS_ATTRIBUTE);
			}
		});
		installMethod(new BuiltinMethod(Constants.DEL_INDEX) {
			public Object invoke(Object... args) {
				return Runtime.invokeMethod(args[0], Constants.DEL_ATTRIBUTE);
			}
		});
	}
}
