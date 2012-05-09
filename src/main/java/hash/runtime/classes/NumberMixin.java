package hash.runtime.classes;

import hash.lang.Function;
import hash.lang.Hash;
import hash.util.Check;
import hash.util.Err;

public class NumberMixin extends Hash {

	public NumberMixin() {
		put("+##", new Function() {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				if (!(other instanceof Number))
					Err.binaryOperatorNotImplemented("+", self, other);
				Number rv = ((Number) self).doubleValue()
						+ ((Number) other).doubleValue();
				if (Check.integerResult(self, other))
					return rv.longValue();
				return rv.doubleValue();
			}
		});
		put("-##", new Function() {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				if (!(other instanceof Number))
					Err.binaryOperatorNotImplemented("-", self, other);
				Number rv = ((Number) self).doubleValue()
						- ((Number) other).doubleValue();
				if (Check.integerResult(self, other))
					return rv.longValue();
				return rv.doubleValue();
			}
		});
		put("*##", new Function() {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				if (!(other instanceof Number))
					Err.binaryOperatorNotImplemented("*", self, other);
				Number rv = ((Number) self).doubleValue()
						* ((Number) other).doubleValue();
				if (Check.integerResult(self, other))
					return rv.longValue();
				return rv.doubleValue();
			}
		});
		put("/##", new Function() {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				if (!(other instanceof Number))
					Err.binaryOperatorNotImplemented("/", self, other);
				Number rv = ((Number) self).doubleValue()
						/ ((Number) other).doubleValue();
				if (Check.integerResult(self, other))
					return rv.longValue();
				return rv.doubleValue();
			}
		});
		put("%##", new Function() {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				if (!(other instanceof Number))
					Err.binaryOperatorNotImplemented("+", self, other);
				Number rv = ((Number) self).doubleValue()
						% ((Number) other).doubleValue();
				if (Check.integerResult(self, other))
					return rv.longValue();
				return rv.doubleValue();
			}
		});
	}
}
