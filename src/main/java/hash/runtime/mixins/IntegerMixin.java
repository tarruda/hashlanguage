package hash.runtime.mixins;

import hash.runtime.functions.BinaryOperator;
import hash.runtime.functions.UnaryOperator;
import hash.util.Check;
import hash.util.Err;
import hash.util.Types;

public class IntegerMixin extends Mixin {

	public IntegerMixin() {
		installMethod(new UnaryOperator("~") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 1);
				Object self = args[0];
				Long rv = ~((Number) self).longValue();
				return Types.integerNumber(rv.longValue());
			}
		});
		installMethod(new BinaryOperator("<<") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				if (!Types.areIntegers(self, other))
					throw Err.binaryOperatorNotImplemented(op, self, other);
				Number rv = ((Number) self).longValue() << ((Number) other)
						.longValue();
				return Types.integerNumber(rv.longValue());
			}
		});
		installMethod(new BinaryOperator(">>") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				if (!Types.areIntegers(self, other))
					throw Err.binaryOperatorNotImplemented(op, self, other);
				Number rv = ((Number) self).longValue() >> ((Number) other)
						.longValue();
				return Types.integerNumber(rv.longValue());
			}
		});
		installMethod(new BinaryOperator("&") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				if (!Types.areIntegers(self, other))
					throw Err.binaryOperatorNotImplemented(op, self, other);
				Number rv = ((Number) self).longValue()
						& ((Number) other).longValue();
				return Types.integerNumber(rv.longValue());
			}
		});
		installMethod(new BinaryOperator("|") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				if (!Types.areIntegers(self, other))
					throw Err.binaryOperatorNotImplemented(op, self, other);
				Number rv = ((Number) self).longValue()
						| ((Number) other).longValue();
				return Types.integerNumber(rv.longValue());
			}
		});
		installMethod(new BinaryOperator("^") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				if (!Types.areIntegers(self, other))
					throw Err.binaryOperatorNotImplemented(op, self, other);
				Number rv = ((Number) self).longValue()
						^ ((Number) other).longValue();
				return Types.integerNumber(rv.longValue());
			}
		});
	}
}
