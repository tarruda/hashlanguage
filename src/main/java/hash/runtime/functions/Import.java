package hash.runtime.functions;

import hash.runtime.generators.HashAdapter;
import hash.util.Check;
import hash.util.Constants;
import hash.util.Err;

public class Import extends BuiltinFunction {

	public Object invoke(Object... args) {
		Check.numberOfArgs(args, 2);
		String name = (String) args[1];
		Class<?> klass;
		try {
			klass = Class.forName(name);
		} catch (ClassNotFoundException e) {
			throw Err.ex(e);
		}
		return HashAdapter.getHashClass(klass);
	}

	@Override
	public String getName() {
		return Constants.IMPORT;
	}

}
