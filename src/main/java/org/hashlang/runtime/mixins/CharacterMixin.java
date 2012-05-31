package org.hashlang.runtime.mixins;

import org.hashlang.runtime.AppRuntime;
import org.hashlang.runtime.functions.BinaryOperator;
import org.hashlang.runtime.operations.StringOperations;
import org.hashlang.util.Check;
import org.hashlang.util.Err;

@SuppressWarnings("serial")
public class CharacterMixin extends Mixin {

	public CharacterMixin(AppRuntime r) {
		super(r);
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
