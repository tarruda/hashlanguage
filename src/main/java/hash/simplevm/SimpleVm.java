package hash.simplevm;

import hash.lang.Context;

public class SimpleVm {

	public static Object execute(Instruction[] instructions,
			TryCatchBlock[] tryCatchBlocks, Context context) throws Throwable {
		int len = instructions.length;
		OperandStack operandStack = new OperandStack();
		InstructionPointer pointer = new InstructionPointer();
		while (pointer.getCurrent() < len - 1) {
			try {
				instructions[pointer.getNext()].exec(context, operandStack,
						pointer);
			} catch (Throwable ex) {
				boolean handled = false;
				for (int i = 0; !handled && i < tryCatchBlocks.length; i++) {
					handled = tryCatchBlocks[i].handle(operandStack, context,
							pointer, ex);
				}
				if (!handled)
					throw ex;
			}
		}
		return operandStack.peek();
	}
}
