package hash.runtime.mixins;

import hash.runtime.functions.BinaryOperator;
import hash.util.Check;
import hash.util.Err;

public class NumberMixin extends Mixin {

	public NumberMixin() {
		installMethod(new BinaryOperator("+") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				if (!(other instanceof Number))
					Err.binaryOperatorNotImplemented("+", self, other);
				Number rv = ((Number) self).doubleValue()
						+ ((Number) other).doubleValue();
				if (Check.isIntegerResult(self, other))
					return rv.longValue();
				return rv.doubleValue();
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
				if (Check.isIntegerResult(self, other))
					return rv.longValue();
				return rv.doubleValue();
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
				if (Check.isIntegerResult(self, other))
					return rv.longValue();
				return rv.doubleValue();
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
				if (Check.isIntegerResult(self, other))
					return rv.longValue();
				return rv.doubleValue();
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
				if (Check.isIntegerResult(self, other))
					return rv.longValue();
				return rv.doubleValue();
			}
		});
	}
}
