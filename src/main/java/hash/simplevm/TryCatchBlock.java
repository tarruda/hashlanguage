package hash.simplevm;

import hash.runtime.AppRuntime;
import hash.runtime.Context;

class TryCatchBlock {

	private int tryStart;
	private int tryEnd;
	private int catchPointer;
	private String exceptionTypeId;

	public TryCatchBlock(int tryStart, int tryEnd, int catchPointer,
			String exceptionTypeId) {
		this.tryStart = tryStart;
		this.tryEnd = tryEnd;
		this.catchPointer = catchPointer;
		this.exceptionTypeId = exceptionTypeId;
	}

	public boolean handle(AppRuntime runtime, OperandStack operandStack,
			Context local, InstructionPointer pointer, Throwable ex) {
		int p = pointer.p - 1;// where the exception ocurred
		if (p > tryEnd || p < tryStart)
			return false;
		if (exceptionTypeId != null
				&& !runtime.isInstance(ex, local.get(exceptionTypeId)))
			return false;
		pointer.p = catchPointer;
		operandStack.push(ex);
		return true;
	}
}
