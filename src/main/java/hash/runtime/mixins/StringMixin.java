package hash.runtime.mixins;

import hash.runtime.functions.BinaryOperator;
import hash.util.Check;
import hash.util.Err;
import hash.util.StringHelpers;

public class StringMixin extends Mixin {

	public StringMixin() {
		installMethod(new BinaryOperator("+") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				if (other.getClass() != String.class)
					Err.binaryOperatorNotImplemented(op, self, other);
				return ((String) self) + ((String) other);
			}
		});
		installMethod(new BinaryOperator("*") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				if (other.getClass() != Integer.class)
					Err.binaryOperatorNotImplemented(op, self, other);
				return StringHelpers.multiplicateString((String) self,
						(Integer) other);
			}
		});

	}
}
