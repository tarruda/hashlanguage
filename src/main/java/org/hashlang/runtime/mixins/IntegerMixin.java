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
public class IntegerMixin extends Mixin {

	
	public IntegerMixin(AppRuntime r) {
		super(r);
		installMethod(new BuiltinMethod(Constants.COMPARE_TO) {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				if (!(other instanceof Number))
					throw Err.illegalArg(name, Types.name(other));
				return Double.compare(((Number) self).doubleValue(),
						((Number) other).doubleValue());
			}
		});
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
		installMethod(new BinaryOperator(">>>") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				if (!Types.areIntegers(self, other))
					throw Err.binaryOperatorNotImplemented(op, self, other);
				Number rv = ((Number) self).longValue() >>> ((Number) other)
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
