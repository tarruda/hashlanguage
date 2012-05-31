package org.hashlang.runtime.mixins;


import java.util.regex.Pattern;

import org.hashlang.runtime.AppRuntime;
import org.hashlang.runtime.functions.BinaryOperator;
import org.hashlang.util.Check;
import org.hashlang.util.Err;

@SuppressWarnings("serial")
public class RegexMixin extends Mixin {

	public RegexMixin(AppRuntime r) {
		super(r);
		installMethod(new BinaryOperator("=~") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Pattern self = (Pattern) args[0];
				Object other = args[1];
				if (!(other instanceof CharSequence))
					throw Err.binaryOperatorNotImplemented(op, self, other);
				return self.matcher((CharSequence) other).matches();
			}
		});
	}
}
