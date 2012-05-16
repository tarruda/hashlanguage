package hash.runtime.mixins;

import hash.runtime.functions.BinaryOperator;
import hash.util.Check;
import hash.util.Err;

import java.util.regex.Pattern;

@SuppressWarnings("serial")
public class RegexMixin extends Mixin {

	public static final RegexMixin INSTANCE = new RegexMixin();

	private RegexMixin() {
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
