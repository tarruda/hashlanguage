package hash.runtime.mixins;

import hash.runtime.functions.BinaryOperator;
import hash.runtime.operations.StringOperations;
import hash.util.Check;
import hash.util.Err;

@SuppressWarnings("serial")
public class CharacterMixin extends Mixin {

	public static final CharacterMixin INSTANCE = new CharacterMixin();

	private CharacterMixin() {		
		installMethod(new BinaryOperator("+") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				if (other.getClass() != String.class
						&& other.getClass() != Character.class)
					throw Err.binaryOperatorNotImplemented(op, self, other);
				return self.toString() + other.toString();
			}
		});
		installMethod(new BinaryOperator("*") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Object self = args[0];
				Object other = args[1];
				if (other.getClass() != Integer.class)
					throw Err.binaryOperatorNotImplemented(op, self, other);
				return StringOperations.multiplication(self.toString(),
						(Integer) other);
			}
		});
	}
}
