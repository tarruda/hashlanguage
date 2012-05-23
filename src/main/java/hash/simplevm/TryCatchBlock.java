package hash.simplevm;

import hash.lang.Context;
import hash.runtime.Runtime;

public class TryCatchBlock {

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

	public boolean handle(OperandStack operandStack, Context local, InstructionPointer pointer,
			Throwable ex) {
		int ip = pointer.getCurrent();
		if (ip > tryEnd || ip < tryStart)
			return false;
		if (exceptionTypeId != null
				&& !Runtime.isInstance(ex, local.get(exceptionTypeId)))
			return false;
		pointer.setNext(catchPointer);
		operandStack.push(ex);
		return true;
	}
}
