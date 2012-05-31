package org.hashlang.runtime.mixins;

import org.hashlang.runtime.AppRuntime;
import org.hashlang.runtime.functions.BuiltinMethod;
import org.hashlang.util.Check;
import org.hashlang.util.Constants;

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
