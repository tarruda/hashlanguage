package hash.runtime.mixins;

import hash.runtime.Lookup;
import hash.runtime.functions.BinaryOperator;
import hash.runtime.functions.BuiltinMethod;
import hash.runtime.functions.UnaryOperator;
import hash.util.Check;
import hash.util.Constants;
import hash.util.Err;
import hash.util.Types;

public class NumberMixin extends Mixin {

	public static final NumberMixin INSTANCE = new NumberMixin();

	private NumberMixin() {
		installMethod(new BuiltinMethod(Constants.BOOLEAN_VALUE) {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 1);
				return ((Number) args[0]).doubleValue() != 0;
			}
		});
		// TODO optimize these operations later
		installMethod(new UnaryOperator("-") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 1);
				Object self = args[0];
				Number rv = -((Number) self).doubleValue();
				if (Types.isInteger(self))
					return Types.integerNumber(rv.longValue());
				return Types.floatNumber(rv.doubleValue());
			}
		});
		installMethod(new BinaryOperator("+") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				if (!(other instanceof Number))
					throw Err.binaryOperatorNotImplemented(op, self, other);
				Number rv = ((Number) self).doubleValue()
						+ ((Number) other).doubleValue();
				if (Types.areIntegers(self, other))
					return Types.integerNumber(rv.longValue());
				return Types.floatNumber(rv.doubleValue());
			}
		});
		installMethod(new BinaryOperator("-") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				if (!(other instanceof Number))
					throw Err.binaryOperatorNotImplemented(op, self, other);
				Number rv = ((Number) self).doubleValue()
						- ((Number) other).doubleValue();
				if (Types.areIntegers(self, other))
					return Types.integerNumber(rv.longValue());
				return Types.floatNumber(rv.doubleValue());
			}
		});
		installMethod(new BinaryOperator("*") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				if (other.getClass() == String.class)
					return Lookup.invokeBinaryOperator(op, other, self);
				if (!(other instanceof Number))
					throw Err.binaryOperatorNotImplemented(op, self, other);
				Number rv = ((Number) self).doubleValue()
						* ((Number) other).doubleValue();
				if (Types.areIntegers(self, other))
					return Types.integerNumber(rv.longValue());
				return Types.floatNumber(rv.doubleValue());
			}
		});
		installMethod(new BinaryOperator("/") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				if (!(other instanceof Number))
					throw Err.binaryOperatorNotImplemented(op, self, other);
				Number rv = ((Number) self).doubleValue()
						/ ((Number) other).doubleValue();
				if (Types.areIntegers(self, other))
					return Types.integerNumber(rv.longValue());
				return Types.floatNumber(rv.doubleValue());
			}
		});
		installMethod(new BinaryOperator("%") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				if (!(other instanceof Number))
					throw Err.binaryOperatorNotImplemented(op, self, other);
				Number rv = ((Number) self).doubleValue()
						% ((Number) other).doubleValue();
				if (Types.areIntegers(self, other))
					return Types.integerNumber(rv.longValue());
				return Types.floatNumber(rv.doubleValue());
			}
		});
		installMethod(new BinaryOperator("**") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				if (!(other instanceof Number))
					throw Err.binaryOperatorNotImplemented(op, self, other);
				Number rv = Math.pow(((Number) self).doubleValue(),
						((Number) other).doubleValue());
				if (Types.areIntegers(self, other))
					return Types.integerNumber(rv.longValue());
				return Types.floatNumber(rv.doubleValue());
			}
		});
	}
}
