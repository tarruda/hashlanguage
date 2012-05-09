package hash.runtime.classes;

import hash.lang.Function;
import hash.lang.Hash;
import hash.runtime.Lookup;
import hash.util.Check;
import hash.util.Err;

public class ObjectMixin extends Hash {

	private static final String COMPARE_TO = "compareTo";
	
	public ObjectMixin() {
		
		put("==##", new Function() {
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
		put("!=##", new Function() {
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
		put(">=##", new Function() {
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
		put(">##", new Function() {
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
		put("<=##", new Function() {
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
		put("<##", new Function() {
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
