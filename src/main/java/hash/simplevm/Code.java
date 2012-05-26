package hash.simplevm;

import java.util.ArrayList;
import java.util.List;

public class Code {
	private List<Instruction> instructions = new ArrayList<Instruction>();
	private List<TryCatchBlock> tryCatchBlocks = new ArrayList<TryCatchBlock>();

	public void add(Instruction inst) {
		instructions.add(inst);
	}

	public void addTryCatchBlock(int tryStart, int tryEnd, int catchPointer,
			String exceptionTypeId) {
		tryCatchBlocks.add(new TryCatchBlock(tryStart, tryEnd, catchPointer,
				exceptionTypeId));
	}

	public int size() {
		return instructions.size();
	}

	public Instruction[] getInstructions() {
		Instruction[] rv = new Instruction[instructions.size()];
		instructions.toArray(rv);
		return rv;
	}

	public TryCatchBlock[] getTryCatchBlocks() {
		TryCatchBlock[] rv = new TryCatchBlock[tryCatchBlocks.size()];
		tryCatchBlocks.toArray(rv);
		return rv;
	}
}
