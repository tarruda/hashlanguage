package hash.runtime.functions;

import hash.runtime.Runtime;
import hash.util.Check;
import hash.util.Constants;

public class Import extends BuiltinFunction {

	public Object invoke(Object... args) {
		Check.numberOfArgs(args, 2);
		String name = (String) args[1];
		return Runtime.doImport(name);
	}

	@Override
	public String getName() {
		return Constants.IMPORT;
	}

}
