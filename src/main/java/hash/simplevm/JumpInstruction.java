package hash.simplevm;

import hash.lang.Context;

public class JumpInstruction extends Instruction {
	protected int target;

	public JumpInstruction(String name) {
		super(name);
	}

	public JumpInstruction() {
		this(Integer.MAX_VALUE);
	}

	public JumpInstruction(int target) {
		super("jump");
		this.target = target;
	}

	public int getTarget() {
		return target;
	}

	public void setTarget(int target) {
		this.target = target;
	}

	@Override
	public void exec(Context local, OperandStack operandStack,
			InstructionPointer pointer) throws Throwable {
		pointer.setNext(getTarget());
	}

	@Override
	public String toString() {
		return super.toString() + " " + target;
	}
}
