package org.hashlang.runtime.mixins;

import org.hashlang.runtime.AppRuntime;
import org.hashlang.runtime.functions.BuiltinMethod;
import org.hashlang.util.Check;
import org.hashlang.util.Constants;
import org.hashlang.util.Err;
import org.hashlang.util.Types;

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
