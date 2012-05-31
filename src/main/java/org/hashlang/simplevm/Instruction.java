package org.hashlang.simplevm;

import org.hashlang.runtime.AppRuntime;
import org.hashlang.runtime.Context;

public abstract class Instruction {
	private String name;

	public Instruction(String name) {
		this.name = name;
	}

	public abstract void exec(AppRuntime runtime, Context local,
			OperandStack operandStack, InstructionPointer pointer, State functionReturn) throws Throwable;

	@Override
	public String toString() {
		return name;
	}

}
