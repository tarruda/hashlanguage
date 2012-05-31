package org.hashlang.simplevm;

import org.hashlang.runtime.AppRuntime;
import org.hashlang.runtime.Context;

public class SimpleVm {

	public static Object execute(AppRuntime runtime, Instruction[] instructions,
			TryCatchBlock[] tryCatchBlocks, Context locals) throws Throwable {
		return execute(runtime, instructions, tryCatchBlocks, locals,
				new OperandStack(), new InstructionPointer());
	}

	static Object execute(AppRuntime runtime, Instruction[] instructions,
			TryCatchBlock[] tryCatchBlocks, Context locals,
			OperandStack operandStack, InstructionPointer ip) throws Throwable {
		int len = instructions.length;
		State state = new State();
		while (!state.pause && !state.stop && ip.p < len) {
			try {
				instructions[ip.p++]
						.exec(runtime, locals, operandStack, ip, state);
			} catch (Throwable ex) {
				boolean handled = false;
				for (int i = 0; !handled && i < tryCatchBlocks.length; i++) {
					handled = tryCatchBlocks[i].handle(runtime, operandStack,
							locals, ip, ex);
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
