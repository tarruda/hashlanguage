package hash.runtime.mixins;

import hash.runtime.AppRuntime;
import hash.runtime.functions.BuiltinMethod;
import hash.util.Check;
import hash.util.Constants;
import hash.util.Err;
import hash.util.Types;

@SuppressWarnings("serial")
public class FloatMixin extends Mixin {

	public FloatMixin(AppRuntime r) {
		super(r);
		installMethod(new BuiltinMethod(Constants.COMPARE_TO) {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				if (!(other instanceof Number))
					throw Err.illegalArg(name, Types.name(other));
				return Double.compare(((Number) self).doubleValue(),
						((Number) other).doubleValue());
			}
		});
	}
}
