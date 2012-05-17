package hash.runtime.functions;

import hash.runtime.HashObject;
import hash.runtime.Runtime;
import hash.util.Check;
import hash.util.Constants;

import java.util.Map;

public class ClassFactory extends BuiltinFunction {

	public Object invoke(Object... args) {
		Check.numberOfArgs(args, 3);
		return Runtime.createClass((Map) args[1], (HashObject) args[2]);
	}

	@Override
	public String getName() {
		return Constants.CLASS;
	}

}
