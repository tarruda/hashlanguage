package org.hashlang.simplevm;


import java.util.List;

import org.hashlang.runtime.AppRuntime;
import org.hashlang.runtime.Context;
import org.hashlang.runtime.Factory;
import org.hashlang.runtime.Function;
import org.hashlang.runtime.Trampoline;
import org.hashlang.util.Check;
import org.hashlang.util.Constants;
import org.hashlang.util.Err;

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
