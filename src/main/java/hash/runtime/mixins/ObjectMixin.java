package hash.runtime.mixins;

import hash.runtime.Lookup;
import hash.runtime.functions.BinaryOperator;
import hash.util.Check;
import hash.util.Err;

public class ObjectMixin extends Mixin {

	private static final String COMPARE_TO = "compareTo";

	public ObjectMixin() {

		installMethod(new BinaryOperator("==") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				Object resultadoComparacao = null;
				try {
					resultadoComparacao = Lookup.invokeMethod(self, COMPARE_TO,
							other);
				} catch (Exception e) {
					return self.equals(other);
				}
				if (!(resultadoComparacao instanceof Number))
					throw Err.invalidComparisonResult();
				return ((Number) resultadoComparacao).doubleValue() == 0;
			}
		});
		installMethod(new BinaryOperator("!=") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				Object resultadoComparacao = null;
				try {
					resultadoComparacao = Lookup.invokeMethod(self, COMPARE_TO,
							other);
				} catch (Exception e) {
					return self.equals(other);
				}
				if (!(resultadoComparacao instanceof Number))
					throw Err.invalidComparisonResult();
				return ((Number) resultadoComparacao).doubleValue() != 0;
			}
		});
		installMethod(new BinaryOperator(">=") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				Object resultadoComparacao = null;
				try {
					resultadoComparacao = Lookup.invokeMethod(self, COMPARE_TO,
							other);
				} catch (Exception e) {
					return self.equals(other);
				}
				if (!(resultadoComparacao instanceof Number))
					throw Err.invalidComparisonResult();
				return ((Number) resultadoComparacao).doubleValue() >= 0;
			}
		});
		installMethod(new BinaryOperator(">") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				Object resultadoComparacao = null;
				try {
					resultadoComparacao = Lookup.invokeMethod(self, COMPARE_TO,
							other);
				} catch (Exception e) {
					return self.equals(other);
				}
				if (!(resultadoComparacao instanceof Number))
					throw Err.invalidComparisonResult();
				return ((Number) resultadoComparacao).doubleValue() > 0;
			}
		});
		installMethod(new BinaryOperator("<=") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				Object resultadoComparacao = null;
				try {
					resultadoComparacao = Lookup.invokeMethod(self, COMPARE_TO,
							other);
				} catch (Exception e) {
					return self.equals(other);
				}
				if (!(resultadoComparacao instanceof Number))
					throw Err.invalidComparisonResult();
				return ((Number) resultadoComparacao).doubleValue() <= 0;
			}
		});
		installMethod(new BinaryOperator("<") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				Object resultadoComparacao = null;
				try {
					resultadoComparacao = Lookup.invokeMethod(self, COMPARE_TO,
							other);
				} catch (Exception e) {
					return self.equals(other);
				}
				if (!(resultadoComparacao instanceof Number))
					throw Err.invalidComparisonResult();
				return ((Number) resultadoComparacao).doubleValue() < 0;
			}
		});
	}
}
