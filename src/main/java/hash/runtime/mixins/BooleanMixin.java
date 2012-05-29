package hash.runtime.mixins;

import hash.runtime.AppRuntime;
import hash.runtime.functions.BuiltinMethod;
import hash.util.Check;
import hash.util.Constants;

@SuppressWarnings("serial")
public class BooleanMixin extends Mixin {

	public BooleanMixin(AppRuntime r) {
		super(r);
		installMethod(new BuiltinMethod(Constants.BOOLEAN_VALUE) {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 1);
				return args[0] == Boolean.TRUE;
			}
		});
	}
}
