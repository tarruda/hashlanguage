package hash.runtime.mixins;

import hash.runtime.AppRuntime;
import hash.runtime.functions.BinaryOperator;
import hash.runtime.functions.BuiltinMethod;
import hash.util.Check;
import hash.util.Constants;

import java.util.Map;

@SuppressWarnings("serial")
public class MapMixin extends Mixin {

	
	public MapMixin(AppRuntime r) {
		super(r);
		installMethod(new BinaryOperator("contains") {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				Map self = (Map) args[0];
				Object other = args[1];
				return self.containsKey(other);
			}
		});
		installMethod(new BuiltinMethod(Constants.SET_ATTRIBUTE) {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 3);
				return ((Map) args[0]).put(args[1], args[2]);
			}
		});
		installMethod(new BuiltinMethod(Constants.DEL_ATTRIBUTE) {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				return ((Map) args[0]).remove(args[1]);
			}
		});
		installMethod(new BuiltinMethod(Constants.GET_INDEX) {
			public Object invoke(Object... args) {
				Check.numberOfArgs(args, 2);
				return ((Map) args[0]).get(args[1]);
			}
		});
		installMethod(new BuiltinMethod(Constants.SET_INDEX) {
			public Object invoke(Object... args) throws Throwable {
				return runtime.invokeNormalMethod(args[0],
						Constants.SET_ATTRIBUTE);
			}
		});
		installMethod(new BuiltinMethod(Constants.DEL_INDEX) {
			public Object invoke(Object... args) throws Throwable {
				return runtime.invokeNormalMethod(args[0],
						Constants.DEL_ATTRIBUTE);
			}
		});
	}
}
