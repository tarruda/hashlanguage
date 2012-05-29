package hash.runtime.mixins;

import hash.runtime.AppRuntime;
import hash.runtime.functions.BinaryOperator;
import hash.runtime.functions.BuiltinMethod;
import hash.runtime.functions.UnaryOperator;
import hash.util.Check;
import hash.util.Constants;
import hash.util.Err;

@SuppressWarnings("serial")
public class ObjectMixin extends Mixin {
	
	public ObjectMixin(AppRuntime r) {
		super(r);
		installMethod(new BinaryOperator("==") {
			public Object invoke(Object... args) throws Throwable {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				Object comparisonResult = null;
				try {
					comparisonResult = runtime.invokeNormalMethod(self,
							Constants.COMPARE_TO, other);
				} catch (Exception e) {
					return self.equals(other);
				}
				if (!(comparisonResult instanceof Integer))
					throw Err.invalidComparisonResult();
				return ((Integer) comparisonResult) == 0;
			}
		});
		installMethod(new BinaryOperator("!=") {
			public Object invoke(Object... args) throws Throwable {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				Object comparisonResult = null;
				try {
					comparisonResult = runtime.invokeNormalMethod(self,
							Constants.COMPARE_TO, other);
				} catch (Exception e) {
					return !self.equals(other);
				}
				if (!(comparisonResult instanceof Integer))
					throw Err.invalidComparisonResult();
				return ((Integer) comparisonResult) != 0;
			}
		});
		installMethod(new BinaryOperator(">=") {
			public Object invoke(Object... args) throws Throwable {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				Object comparisonResult = null;
				comparisonResult = runtime.invokeNormalMethod(self,
						Constants.COMPARE_TO, other);
				if (!(comparisonResult instanceof Integer))
					throw Err.invalidComparisonResult();
				return ((Integer) comparisonResult) >= 0;
			}
		});
		installMethod(new BinaryOperator(">") {
			public Object invoke(Object... args) throws Throwable {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				Object comparisonResult = null;
				comparisonResult = runtime.invokeNormalMethod(self,
						Constants.COMPARE_TO, other);
				if (!(comparisonResult instanceof Integer))
					throw Err.invalidComparisonResult();
				return ((Integer) comparisonResult) > 0;
			}
		});
		installMethod(new BinaryOperator("<=") {
			public Object invoke(Object... args) throws Throwable {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				Object comparisonResult = null;
				comparisonResult = runtime.invokeNormalMethod(self,
						Constants.COMPARE_TO, other);
				if (!(comparisonResult instanceof Integer))
					throw Err.invalidComparisonResult();
				return ((Integer) comparisonResult) <= 0;
			}
		});
		installMethod(new BinaryOperator("<") {
			public Object invoke(Object... args) throws Throwable {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				Object comparisonResult = null;
				comparisonResult = runtime.invokeNormalMethod(self,
						Constants.COMPARE_TO, other);
				if (!(comparisonResult instanceof Integer))
					throw Err.invalidComparisonResult();
				return ((Integer) comparisonResult) < 0;
			}
		});
		installMethod(new BinaryOperator("&&") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				return args[1];
			}
		});
		installMethod(new BinaryOperator("||") {
			public Object invoke(Object... args) throws Throwable {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				Object selfValue = runtime.invokeNormalMethod(self,
						Constants.BOOLEAN_VALUE);
				if (selfValue.getClass() == Boolean.class
						&& ((Boolean) selfValue).booleanValue())
					return self;
				return other;
			}
		});
		installMethod(new UnaryOperator("!") {
			public Object invoke(Object... args) throws Throwable {
				Check.numberOfArgs(args, 1);
				Object self = args[0];
				Object booleanValue = runtime.invokeNormalMethod(self,
						Constants.BOOLEAN_VALUE);
				if (booleanValue == Boolean.TRUE)
					return false;
				return true;
			}
		});
		installMethod(new BuiltinMethod(Constants.BOOLEAN_VALUE) {
			public Object invoke(Object... args) {
				return true;
			}
		});
		installMethod(new BuiltinMethod(Constants.GET_ATTRIBUTE) {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				return runtime.lookup(args[0], args[1]);
			}
		});
		installMethod(new BuiltinMethod(Constants.GET_INDEX) {
			public Object invoke(Object... args) throws Throwable {
				return runtime.invokeNormalMethod(args[0],
						Constants.GET_ATTRIBUTE);
			}
		});
	}
}
