package hash.runtime.mixins;

import hash.runtime.AppRuntime;
import hash.runtime.functions.BinaryOperator;
import hash.runtime.functions.BuiltinMethod;
import hash.runtime.operations.Common;
import hash.runtime.operations.StringOperations;
import hash.util.Check;
import hash.util.Constants;
import hash.util.Err;

@SuppressWarnings("serial")
public class StringMixin extends Mixin {

	public StringMixin(AppRuntime r) {
		super(r);
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
					throw Err.binaryOperatorNotImplemented(op, self, other);
				return ((String) self) + ((String) other);
			}
		});
		installMethod(new BinaryOperator("*") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				if (other.getClass() != Integer.class)
					throw Err.binaryOperatorNotImplemented(op, self, other);
				return StringOperations.multiplication((String) self,
						(Integer) other);
			}
		});
		installMethod(new BuiltinMethod(Constants.GET_INDEX) {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				String self = (String) args[0];
				if (args[1] == null)
					throw Err.nullIndex();
				int idx = (Integer) args[1];
				return self.charAt(Common.calculateAbsoluteIndex(idx,
						self.length()));
			}
		});
		installMethod(new BuiltinMethod(Constants.GET_SLICE) {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 4);
				String str = (String) args[0];
				Object lowerBound = args[1];
				Object upperBound = args[2];
				Object step = args[3];
				int start = 0;
				int end = str.length() - 1;
				int inc = 1;
				if (lowerBound instanceof Integer)
					start = (Integer) lowerBound;
				if (upperBound instanceof Integer)
					end = (Integer) upperBound;
				if (step instanceof Integer)
					inc = (Integer) step;
				return StringOperations.slice(str, start, end, inc);
			}
		});
	}
}
