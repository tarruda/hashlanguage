package org.hashlang.simplevm;

import org.hashlang.runtime.AppRuntime;
import org.hashlang.runtime.Context;

public class GotoInstruction extends Instruction {
	protected int target;

	public GotoInstruction(String name) {
		super(name);
	}

	public GotoInstruction() {
		this(Integer.MAX_VALUE);
	}

	public GotoInstruction(int target) {
		super("goto");
		this.target = target;
	}

	public int getTarget() {
		return target;
	}

	public void setTarget(int target) {
		this.target = target;
	}

	@Override
	public void exec(AppRuntime runtime, Context local,
			OperandStack operandStack, InstructionPointer ip, State functionReturn)
			throws Throwable {
		ip.p = getTarget();
	}

	@Override
	public String toString() {
		return super.toString() + " " + target;
	}
}
