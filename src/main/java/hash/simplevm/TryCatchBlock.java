package hash.simplevm;

import hash.runtime.Context;
import hash.runtime.Runtime;

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

	public boolean handle(OperandStack operandStack, Context local,
			InstructionPointer pointer, Throwable ex) {
		int p = pointer.p - 1;// where the exception ocurred
		if (p > tryEnd || p < tryStart)
			return false;
		if (exceptionTypeId != null
				&& !Runtime.isInstance(ex, local.get(exceptionTypeId)))
			return false;
		pointer.p = catchPointer;
		operandStack.push(ex);
		return true;
	}
}
