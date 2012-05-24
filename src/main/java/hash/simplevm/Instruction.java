package hash.simplevm;

import hash.lang.Context;

public abstract class Instruction {
	private String name;

	public Instruction(String name) {
		this.name = name;
	}

	public abstract void exec(Context local, OperandStack operandStack,
			InstructionPointer pointer, ExecutionState functionReturn) throws Throwable;

	@Override
	public String toString() {
		return name;
	}
}
