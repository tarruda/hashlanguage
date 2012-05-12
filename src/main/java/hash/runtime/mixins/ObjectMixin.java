package hash.runtime.mixins;

import hash.runtime.Runtime;
import hash.runtime.bridge.HashToJava;
import hash.runtime.functions.BinaryOperator;
import hash.runtime.functions.BuiltinMethod;
import hash.util.Check;
import hash.util.Constants;
import hash.util.Err;

import java.util.Map;

@SuppressWarnings("serial")
public class ObjectMixin extends Mixin {

	public static final ObjectMixin INSTANCE = new ObjectMixin();

	private ObjectMixin() {
		installMethod(new BinaryOperator("==") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				Object comparisonResult = null;
				try {
					comparisonResult = Runtime.invokeMethod(self,
							Constants.COMPARE_TO, other);
				} catch (Exception e) {
					return self.equals(other);
				}
				if (!(comparisonResult instanceof Number))
					throw Err.invalidComparisonResult();
				return ((Number) comparisonResult).doubleValue() == 0;
			}
		});
		installMethod(new BinaryOperator("!=") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				Object comparisonResult = null;
				try {
					comparisonResult = Runtime.invokeMethod(self,
							Constants.COMPARE_TO, other);
				} catch (Exception e) {
					return !self.equals(other);
				}
				if (!(comparisonResult instanceof Number))
					throw Err.invalidComparisonResult();
				return ((Number) comparisonResult).doubleValue() != 0;
			}
		});
		installMethod(new BinaryOperator(">=") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				Object comparisonResult = null;
				comparisonResult = Runtime.invokeMethod(self,
						Constants.COMPARE_TO, other);
				if (!(comparisonResult instanceof Number))
					throw Err.invalidComparisonResult();
				return ((Number) comparisonResult).doubleValue() >= 0;
			}
		});
		installMethod(new BinaryOperator(">") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				Object comparisonResult = null;
				comparisonResult = Runtime.invokeMethod(self,
						Constants.COMPARE_TO, other);
				if (!(comparisonResult instanceof Number))
					throw Err.invalidComparisonResult();
				return ((Number) comparisonResult).doubleValue() > 0;
			}
		});
		installMethod(new BinaryOperator("<=") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				Object comparisonResult = null;
				comparisonResult = Runtime.invokeMethod(self,
						Constants.COMPARE_TO, other);
				if (!(comparisonResult instanceof Number))
					throw Err.invalidComparisonResult();
				return ((Number) comparisonResult).doubleValue() <= 0;
			}
		});
		installMethod(new BinaryOperator("<") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				Object comparisonResult = null;
				comparisonResult = Runtime.invokeMethod(self,
						Constants.COMPARE_TO, other);
				if (!(comparisonResult instanceof Number))
					throw Err.invalidComparisonResult();
				return ((Number) comparisonResult).doubleValue() < 0;
			}
		});
		installMethod(new BinaryOperator("&&") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				return args[1];
			}
		});
		installMethod(new BinaryOperator("||") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				Object selfValue = Runtime.invokeMethod(self,
						Constants.BOOLEAN_VALUE);
				if (selfValue.getClass() == Boolean.class
						&& ((Boolean) selfValue).booleanValue())
					return self;
				return other;
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
				Object self = args[0];
				Object key = args[1];
				Map cls = HashToJava.getClass(self);
				Object rv = null;
				while (rv == null && cls != null) {
					rv = cls.get(key);
					cls = HashToJava.getSuperclass(cls);
				}
				return rv;
			}
		});
		installMethod(new BuiltinMethod(Constants.GET_INDEX) {
			public Object invoke(Object... args) {
				return Runtime.invokeMethod(args[0], Constants.GET_ATTRIBUTE);
			}
		});
	}
}
