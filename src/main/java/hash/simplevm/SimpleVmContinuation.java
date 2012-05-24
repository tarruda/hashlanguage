package hash.simplevm;

import hash.lang.Context;
import hash.lang.Continuation;
import hash.util.Err;

public class SimpleVmContinuation implements Continuation {

	private Context locals;
	private Instruction[] instructions;
	private TryCatchBlock[] tryCatchBlocks;
	private OperandStack operandStack;
	private InstructionPointer ip;
	private Object next;
	private boolean hasNext;

	public SimpleVmContinuation(Context locals, Instruction[] instructions,
			TryCatchBlock[] tryCatchBlocks, OperandStack operandStack,
			InstructionPointer ip) throws Throwable {
		this.locals = locals;
		this.instructions = instructions;
		this.tryCatchBlocks = tryCatchBlocks;
		this.operandStack = operandStack;
		this.ip = ip;
		resume(null);
	}

	public Object resume(Object arg) throws Throwable {
		if (ip.p >= instructions.length)
			if (hasNext) {
				hasNext = false;
				return next;
			} else
				throw Err.illegalState("Function has finished its execution");
		Object rv = next;
		operandStack.push(arg);
		Object retVal = SimpleVm.execute(instructions, tryCatchBlocks, locals,
				operandStack, ip);
		while (!(retVal instanceof FunctionReturn)
				&& ip.p < instructions.length)
			retVal = SimpleVm.execute(instructions, tryCatchBlocks, locals,
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
