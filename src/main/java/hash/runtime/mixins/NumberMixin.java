package hash.runtime.mixins;

import hash.runtime.functions.BinaryOperator;
import hash.util.Check;
import hash.util.Err;
import hash.util.Numbers;

public class NumberMixin extends Mixin {

	public NumberMixin() {
		// TODO optimize these operations later
		installMethod(new BinaryOperator("+") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				if (!(other instanceof Number))
					Err.binaryOperatorNotImplemented("+", self, other);
				Number rv = ((Number) self).doubleValue()
						+ ((Number) other).doubleValue();
				if (Numbers.isIntegerResult(self, other))
					return Numbers.integerNumber(rv.longValue());
				return Numbers.floatNumber(rv.doubleValue());
			}
		});
		installMethod(new BinaryOperator("-") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				if (!(other instanceof Number))
					Err.binaryOperatorNotImplemented("-", self, other);
				Number rv = ((Number) self).doubleValue()
						- ((Number) other).doubleValue();
				if (Numbers.isIntegerResult(self, other))
					return Numbers.integerNumber(rv.longValue());
				return Numbers.floatNumber(rv.doubleValue());
			}
		});
		installMethod(new BinaryOperator("*") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				if (!(other instanceof Number))
					Err.binaryOperatorNotImplemented("*", self, other);
				Number rv = ((Number) self).doubleValue()
						* ((Number) other).doubleValue();
				if (Numbers.isIntegerResult(self, other))
					return Numbers.integerNumber(rv.longValue());
				return Numbers.floatNumber(rv.doubleValue());
			}
		});
		installMethod(new BinaryOperator("/") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				if (!(other instanceof Number))
					Err.binaryOperatorNotImplemented("/", self, other);
				Number rv = ((Number) self).doubleValue()
						/ ((Number) other).doubleValue();
				if (Numbers.isIntegerResult(self, other))
					return Numbers.integerNumber(rv.longValue());
				return Numbers.floatNumber(rv.doubleValue());
			}
		});
		installMethod(new BinaryOperator("%") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				if (!(other instanceof Number))
					Err.binaryOperatorNotImplemented("+", self, other);
				Number rv = ((Number) self).doubleValue()
						% ((Number) other).doubleValue();
				if (Numbers.isIntegerResult(self, other))
					return Numbers.integerNumber(rv.longValue());
				return Numbers.floatNumber(rv.doubleValue());
			}
		});
	}
}
