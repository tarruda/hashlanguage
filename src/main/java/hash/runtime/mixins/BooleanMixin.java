package hash.runtime.mixins;

import hash.runtime.functions.BuiltinMethod;
import hash.util.Check;
import hash.util.Constants;

@SuppressWarnings("serial")
public class BooleanMixin extends Mixin {

	public static final BooleanMixin INSTANCE = new BooleanMixin();

	private BooleanMixin() {
		installMethod(new BuiltinMethod(Constants.BOOLEAN_VALUE) {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 1);
				return args[0] == Boolean.TRUE;
			}
		});
	}
}
