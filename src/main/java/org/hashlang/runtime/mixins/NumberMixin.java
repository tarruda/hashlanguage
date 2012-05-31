package org.hashlang.runtime.mixins;

import org.hashlang.runtime.AppRuntime;
import org.hashlang.runtime.functions.BinaryOperator;
import org.hashlang.runtime.functions.BuiltinMethod;
import org.hashlang.runtime.functions.UnaryOperator;
import org.hashlang.util.Check;
import org.hashlang.util.Constants;
import org.hashlang.util.Err;
import org.hashlang.util.Types;

@SuppressWarnings("serial")
public class NumberMixin extends Mixin {

	public NumberMixin(AppRuntime r) {
		super(r);
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
			public Object invoke(Object... args) throws Throwable {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				if (other.getClass() == String.class)
					return runtime.invokeBinaryOperator(op, other, self);
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
