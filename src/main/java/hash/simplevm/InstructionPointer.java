package hash.simplevm;

public class InstructionPointer {
	private int current = -1;

	public int getCurrent() {
		return current;
	}
	
	public int getNext() {
		return ++current;
	}

	public void setNext(int current) {
		this.current = current - 1;
	}
}
