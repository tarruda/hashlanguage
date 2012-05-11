package hash.runtime.mixins;

import hash.runtime.exceptions.IllegalArgTypeException;
import hash.runtime.functions.BuiltinMethod;
import hash.util.Check;
import hash.util.Constants;
import hash.util.Types;

public class FloatMixin extends Mixin {

	public static final FloatMixin INSTANCE = new FloatMixin();
	
	private FloatMixin() {
		installMethod(new BuiltinMethod(Constants.COMPARE_TO) {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				if (!(other instanceof Number))
					throw new IllegalArgTypeException(name, Types.name(other));
				return Double.compare(((Number) self).doubleValue(),
						((Number) other).doubleValue());
			}
		});
	}
}
