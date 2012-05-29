package hash.runtime.mixins;

import hash.runtime.AppRuntime;
import hash.runtime.functions.BinaryOperator;
import hash.util.Check;
import hash.util.Err;

import java.util.regex.Pattern;

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
