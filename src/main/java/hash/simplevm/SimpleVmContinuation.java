package hash.simplevm;

import hash.runtime.AppRuntime;
import hash.runtime.Context;
import hash.runtime.Continuation;
import hash.util.Err;

class SimpleVmContinuation implements Continuation {

	private AppRuntime runtime;
	private Context locals;
	private Instruction[] instructions;
	private TryCatchBlock[] tryCatchBlocks;
	private OperandStack operandStack;
	private InstructionPointer ip;
	private Object next;
	private boolean hasNext = true;
	private boolean started = false;

	public SimpleVmContinuation(AppRuntime runtime, Context locals, Instruction[] instructions,
			TryCatchBlock[] tryCatchBlocks, OperandStack operandStack,
			InstructionPointer ip) throws Throwable {
		this.runtime = runtime;
		this.locals = locals;
		this.instructions = instructions;
		this.tryCatchBlocks = tryCatchBlocks;
		this.operandStack = operandStack;
		this.ip = ip;
	}

	public Object resume() throws Throwable {
		return resume(null);
	}

	public Object resume(Object arg) throws Throwable {
		if (!started) {
			started = true;
			resume();
		}
		if (ip.p >= instructions.length)
			if (hasNext) {
				hasNext = false;
				return next;
			} else
				throw Err.illegalState("Function has finished its execution");
		Object rv = next;
		operandStack.push(arg);
		Object retVal = SimpleVm.execute(runtime, instructions, tryCatchBlocks, locals,
				operandStack, ip);
		while (!(retVal instanceof FunctionReturn)
 				&& ip.p < instructions.length)
			retVal = SimpleVm.execute(runtime, instructions, tryCatchBlocks, locals,
					operandStack, ip);
		if (retVal instanceof FunctionReturn) {
			next = ((FunctionReturn) retVal).value;
			hasNext = true;
		} else
			hasNext = false;
		return rv;
	}

	public boolean isAlive() {
		return hasNext;
	}

}
