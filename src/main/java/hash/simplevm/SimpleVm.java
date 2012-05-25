package hash.simplevm;

import hash.runtime.Context;

public class SimpleVm {

	public static Object execute(Instruction[] instructions,
			TryCatchBlock[] tryCatchBlocks, Context locals) throws Throwable {
		return execute(instructions, tryCatchBlocks, locals, new OperandStack(
				locals), new InstructionPointer());
	}

	static Object execute(Instruction[] instructions,
			TryCatchBlock[] tryCatchBlocks, Context locals,
			OperandStack operandStack, InstructionPointer ip) throws Throwable {
		int len = instructions.length;
		ExecutionState state = new ExecutionState();
		while (!state.pause && !state.stop && ip.p < len) {
			try {
				instructions[ip.p++].exec(locals, operandStack, ip, state);
			} catch (Throwable ex) {
				boolean handled = false;
				for (int i = 0; !handled && i < tryCatchBlocks.length; i++) {
					handled = tryCatchBlocks[i].handle(operandStack, locals,
							ip, ex);
				}
				if (!handled)
					throw ex;
			}
		}
		if (state.stop)
			ip.p = len;
		if (operandStack.size() > 0)
			return operandStack.pop();
		return null;
	}
}
