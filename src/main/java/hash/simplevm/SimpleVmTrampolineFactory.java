package hash.simplevm;

import hash.runtime.AppRuntime;
import hash.runtime.Context;
import hash.runtime.Factory;
import hash.runtime.Function;
import hash.runtime.Trampoline;
import hash.util.Check;
import hash.util.Constants;
import hash.util.Err;

import java.util.List;

class SimpleVmTrampolineFactory implements Function {

	private AppRuntime runtime;
	private Context definingContext;
	private List parameters;
	private Instruction[] instructions;
	private TryCatchBlock[] tryCatchBlocks;
	private boolean isMethod;

	public SimpleVmTrampolineFactory(AppRuntime runtime, Context definingContext,
			List parameters, Code code, boolean isMethod) {
		this.runtime = runtime;
		this.definingContext = definingContext;
		this.parameters = parameters;
		this.instructions = code.getInstructions();
		this.tryCatchBlocks = code.getTryCatchBlocks();
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
		return new Trampoline(new SimpleVmContinuation(runtime, context, instructions,
				tryCatchBlocks, new OperandStack(), new InstructionPointer()));
	}
}
