package hash.simplevm;

import hash.lang.Context;
import hash.parsing.visitors.simplevm.FunctionInterpreter;
import hash.runtime.Factory;
import hash.runtime.Runtime;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Instructions {

	public static Instruction pop() {
		return new Instruction("pop") {
			public void exec(Context local, OperandStack operandStack,
					InstructionPointer pointer) {
				if (operandStack.peek() != null)
					operandStack.pop();
			}
		};
	}

	public static Instruction push(final Object obj) {
		return new Instruction("push") {
			public void exec(Context local, OperandStack operandStack,
					InstructionPointer pointer) {
				operandStack.push(obj);
			}
		};
	}

	public static Instruction pushMap(final int len) {
		return new Instruction("pushMap") {
			public void exec(Context local, OperandStack operandStack,
					InstructionPointer pointer) {
				Map m = Factory.createMap();
				for (int i = 0; i < len; i++) {
					Object value = operandStack.pop();
					Object key = operandStack.pop();
					m.put(key, value);
				}
				operandStack.push(m);
			}
		};
	}

	public static Instruction pushList(final int len) {
		return new Instruction("pushList") {
			public void exec(Context local, OperandStack operandStack,
					InstructionPointer pointer) {
				List l = Factory.createList(len);
				for (int i = 0; i < len; i++)
					l.add(operandStack.pop());
				operandStack.push(l);
			}
		};
	}

	public static Instruction pushSlice() {
		return new Instruction("pushSlice") {
			public void exec(Context local, OperandStack operandStack,
					InstructionPointer pointer) throws Throwable {
				List args = (List) operandStack.pop();
				Object target = operandStack.pop();
				operandStack.push(Runtime.getSlice(target, args.get(0),
						args.get(1), args.get(2)));
			}
		};
	}

	public static Instruction pushFunction(final List params, final Code code,
			final boolean isMethod) {
		return new Instruction("pushFunction") {
			public void exec(Context local, OperandStack operandStack,
					InstructionPointer pointer) {
				operandStack.push(new FunctionInterpreter(local, params, code,
						isMethod));
			}
		};
	}

	public static Instruction invokeUnary(final String operator) {
		return new Instruction("invokeUnary") {
			public void exec(Context local, OperandStack operandStack,
					InstructionPointer pointer) throws Throwable {
				Object operand = operandStack.pop();
				operandStack.push(Runtime
						.invokeUnaryOperator(operator, operand));
			}
		};
	}

	public static Instruction invokeBinary(final String operator) {
		return new Instruction("invokeBinary") {
			public void exec(Context local, OperandStack operandStack,
					InstructionPointer pointer) throws Throwable {
				Object right = operandStack.pop();
				Object left = operandStack.pop();
				operandStack.push(Runtime.invokeBinaryOperator(operator, left,
						right));
			}
		};
	}

	public static Instruction getNameRef(final String name, final int l) {
		return new Instruction("getName") {
			public void exec(Context local, OperandStack operandStack,
					InstructionPointer pointer) {
				Context c = Runtime.getContext(l, local);
				operandStack.push(c.get(name));
			}
		};
	}

	public static Instruction setNameRef(final String name, final int l) {
		return new Instruction("setName") {
			public void exec(Context local, OperandStack operandStack,
					InstructionPointer pointer) {
				Context c = Runtime.getContext(l, local);
				Object val = operandStack.pop();
				c.put(name, val);
				operandStack.push(val);
			}
		};
	}

	public static Instruction areSame() {
		return new Instruction("areSame") {
			public void exec(Context local, OperandStack operandStack,
					InstructionPointer pointer) {
				Object right = operandStack.pop();
				Object left = operandStack.pop();
				operandStack.push(left == right);
			}
		};
	}

	public static Instruction getAttr(final String key) {
		return new Instruction("getAttr") {
			public void exec(Context local, OperandStack operandStack,
					InstructionPointer pointer) throws Throwable {
				operandStack
						.push(Runtime.getAttribute(operandStack.pop(), key));
			}
		};
	}

	public static Instruction getIndex() {
		return new Instruction("getIndex") {
			public void exec(Context local, OperandStack operandStack,
					InstructionPointer pointer) throws Throwable {
				Object key = operandStack.pop();
				operandStack.push(Runtime.getIndex(operandStack.pop(), key));
			}
		};
	}

	public static Instruction setAttr(final Object key) {
		return new Instruction("setAttr") {
			public void exec(Context local, OperandStack operandStack,
					InstructionPointer pointer) throws Throwable {
				Object target = operandStack.pop();
				Object value = operandStack.pop();
				Runtime.setAttribute(target, key, value);
				operandStack.push(value);
			}
		};
	}

	public static Instruction setIndex() {
		return new Instruction("setIndex") {
			public void exec(Context local, OperandStack operandStack,
					InstructionPointer pointer) throws Throwable {
				Object key = operandStack.pop();
				Object target = operandStack.pop();
				Object value = operandStack.pop();
				Runtime.setIndex(target, key, value);
				operandStack.push(value);
			}
		};
	}

	public static Instruction invokeMethod() {
		return new Instruction("invokeMethod") {
			public void exec(Context local, OperandStack operandStack,
					InstructionPointer pointer) throws Throwable {
				Object methodKey = operandStack.pop();
				Object target = operandStack.pop();
				List args = (List) operandStack.pop();
				operandStack.push(Runtime.invokeNormalMethod(target, methodKey,
						args.toArray()));
			}
		};
	}

	public static Instruction invokeFunction() {
		return new Instruction("invokeFunction") {
			public void exec(Context local, OperandStack operandStack,
					InstructionPointer pointer) throws Throwable {
				Object f = operandStack.pop();
				List args = (List) operandStack.pop();
				operandStack.push(Runtime.invokeFunction(f, args.toArray()));
			}
		};
	}

	public static Instruction ret() {
		return new Instruction("ret") {
			public void exec(Context local, OperandStack operandStack,
					InstructionPointer pointer) {
				operandStack.push(new ReturnValue(operandStack.pop()));
				pointer.setNext(Integer.MAX_VALUE);
			}
		};
	}

	public static Instruction invokeMethod(final String key,
			final boolean hasArgs) {
		return new Instruction("invokeMethod") {
			public void exec(Context local, OperandStack operandStack,
					InstructionPointer pointer) throws Throwable {
				Object target = operandStack.pop();
				if (hasArgs)
					operandStack.push(Runtime.invokeSpecialMethod(target, key,
							((List) operandStack.pop()).toArray()));
				else
					operandStack.push(Runtime.invokeSpecialMethod(target, key));
			}
		};
	}

	public static JumpInstruction jumpIfFalse() {
		return new JumpInstruction("jumpIfFalse") {
			@Override
			public void exec(Context local, OperandStack operandStack,
					InstructionPointer pointer) {
				Boolean val = (Boolean) operandStack.pop();
				if (!val)
					pointer.setNext(getTarget());
			}
		};
	}

	public static JumpInstruction jump(int pointer) {
		return new JumpInstruction(pointer);
	}

	public static JumpInstruction jump() {
		return new JumpInstruction();
	}

	public static Instruction iterator(final String varName) {
		return new Instruction("iterator") {
			public void exec(Context local, OperandStack operandStack,
					InstructionPointer pointer) {
				local.put(varName, Runtime.getIterator(operandStack.pop()));
			}
		};
	}

	public static Instruction iteratorNext(final String varName,
			final String currentItemName) {
		return new Instruction("iteratorNext") {
			public void exec(Context local, OperandStack operandStack,
					InstructionPointer pointer) {
				Iterator it = (Iterator) local.get(varName);
				boolean hasNext = it.hasNext();
				if (hasNext)
					local.put(currentItemName, it.next());
				operandStack.push(hasNext);
			}
		};
	}

	public static Instruction throwTop() {
		return new Instruction("throw") {
			public void exec(Context local, OperandStack operandStack,
					InstructionPointer pointer) throws Throwable {
				throw Runtime.throwableObj(operandStack.pop());
			}
		};
	}
}
