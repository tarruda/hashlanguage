package hash.runtime.mixins;

import hash.runtime.Operations;
import hash.runtime.functions.BinaryOperator;
import hash.runtime.functions.BuiltinMethod;
import hash.util.Check;
import hash.util.Constants;
import hash.util.Err;

@SuppressWarnings("serial")
public class StringMixin extends Mixin {

	public static final StringMixin INSTANCE = new StringMixin();

	private StringMixin() {
		installMethod(new BuiltinMethod(Constants.BOOLEAN_VALUE) {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 1);
				return args[0].toString().length() != 0;
			}
		});
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
				return Operations.multiplicateString((String) self,
						(Integer) other);
			}
		});

	}
}
