package hash.parsing.visitors.simplevm;

import hash.lang.Context;
import hash.lang.Function;
import hash.runtime.Factory;
import hash.simplevm.Code;
import hash.simplevm.ReturnValue;
import hash.simplevm.SimpleVm;
import hash.util.Check;
import hash.util.Constants;
import hash.util.Err;

import java.util.List;

public class FunctionInterpreter implements Function {

	private Context definingContext;
	private List parameters;
	private Code code;
	private boolean isMethod;

	public FunctionInterpreter(Context definingContext, List parameters,
			Code code, boolean isMethod) {
		this.definingContext = definingContext;
		this.parameters = parameters;
		this.code = code;
		this.isMethod = isMethod;
	}

	public Object invoke(Object... args) throws Throwable {
		Check.numberOfArgs(args, parameters.size() + 1);
		Context context = Factory.createContext(definingContext);
		Object self = args[0];
		if (self == null && isMethod)
			throw Err.functionIsMethod();
		context.put(Constants.THIS, self);
		for (int i = 0; i < parameters.size(); i++)
			context.put(parameters.get(i), args[i + 1]);
		Object result = SimpleVm.execute(code.toArray(),
				code.getTryCatchBlocks(), context);
		if (result instanceof ReturnValue)
			return ((ReturnValue) result).getVal();
		return null;
	}

}
