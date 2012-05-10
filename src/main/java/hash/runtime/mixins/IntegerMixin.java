package hash.runtime.mixins;

import hash.runtime.functions.UnaryOperator;
import hash.util.Check;
import hash.util.Numbers;

public class IntegerMixin extends Mixin {

	public IntegerMixin() {
		installMethod(new UnaryOperator("~") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 1);
				Object self = args[0];
				Long rv = ~((Number) self).longValue();
				return Numbers.integerNumber(rv.longValue());
			}
		});
	}
}
