package hash.simplevm;

import hash.runtime.Context;
import hash.runtime.Factory;
import hash.runtime.Runtime;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Instructions {

	public static Instruction pop() {
		return new Instruction("pop") {
			public void exec(Context locals, OperandStack stack,
					InstructionPointer ip, State state) {
				stack.pop();
			}
		};
	}

	public static Instruction save() {
		return new Instruction("save") {
			public void exec(Context locals, OperandStack stack,
					InstructionPointer ip, State state) {
				locals.save(stack.pop());
			}
		};
	}

	public static Instruction push(final Object obj) {
		return new Instruction("push") {
			public void exec(Context locals, OperandStack stack,
					InstructionPointer ip, State state) {
				stack.push(obj);
			}
		};
	}

	public static Instruction pushMap(final int len) {
		return new Instruction("pushMap") {
			public void exec(Context locals, OperandStack stack,
					InstructionPointer ip, State state) {
				Map m = Factory.createMap();
				for (int i = 0; i < len; i++) {
					Object value = stack.pop();
					Object key = stack.pop();
					m.put(key, value);
				}
				stack.push(m);
			}
		};
	}

	public static Instruction pushList(final int len) {
		return new Instruction("pushList") {
			public void exec(Context locals, OperandStack stack,
					InstructionPointer ip, State state) {
				List l = Factory.createList(len);
				for (int i = 0; i < len; i++)
					l.add(stack.pop());
				stack.push(l);
			}
		};
	}

	public static Instruction pushSlice() {
		return new Instruction("pushSlice") {
			public void exec(Context locals, OperandStack stack,
					InstructionPointer ip, State state)
					throws Throwable {
				List args = (List) stack.pop();
				Object target = stack.pop();
				stack.push(Runtime.getSlice(target, args.get(0),
						args.get(1), args.get(2)));
			}
		};
	}

	public static Instruction pushTrampolineFactory(final List params,
			final Code code, final boolean isMethod) {
		return new Instruction("pushContinuationFactory") {
			public void exec(Context locals, OperandStack stack,
					InstructionPointer ip, State state) {
				stack.push(new SimpleVmTrampolineFactory(locals, params,
						code, isMethod));
			}
		};
	}

	public static Instruction pushFunction(final List params, final Code code,
			final boolean isMethod) {
		return new Instruction("pushFunction") {
			public void exec(Context locals, OperandStack stack,
					InstructionPointer ip, State state) {
				stack.push(new SimpleVmFunction(locals, params, code,
						isMethod));
			}
		};
	}

	public static Instruction invokeUnary(final String operator) {
		return new Instruction("invokeUnary") {
			public void exec(Context locals, OperandStack stack,
					InstructionPointer ip, State state)
					throws Throwable {
				Object operand = stack.pop();
				stack.push(Runtime
						.invokeUnaryOperator(operator, operand));
			}
		};
	}

	public static Instruction invokeBinary(final String operator) {
		return new Instruction("invokeBinary") {
			public void exec(Context locals, OperandStack stack,
					InstructionPointer ip, State state)
					throws Throwable {
				Object right = stack.pop();
				Object left = stack.pop();
				stack.push(Runtime.invokeBinaryOperator(operator, left,
						right));
			}
		};
	}

	public static Instruction getNameRef(final String name, final int l) {
		return new Instruction("getName") {
			public void exec(Context locals, OperandStack stack,
					InstructionPointer ip, State state) {
				Context c = Runtime.getContext(l, locals);
				stack.push(c.get(name));
			}
		};
	}

	public static Instruction setNameRef(final String name, final int l) {
		return new Instruction("setName") {
			public void exec(Context locals, OperandStack stack,
					InstructionPointer ip, State state) {
				Context c = Runtime.getContext(l, locals);
				Object val = stack.pop();
				c.put(name, val);
				stack.push(val);
			}
		};
	}

	public static Instruction areSame() {
		return new Instruction("areSame") {
			public void exec(Context locals, OperandStack stack,
					InstructionPointer ip, State state) {
				Object right = stack.pop();
				Object left = stack.pop();
				stack.push(left == right);
			}
		};
	}

	public static Instruction getAttr(final String key) {
		return new Instruction("getAttr") {
			public void exec(Context locals, OperandStack stack,
					InstructionPointer ip, State state)
					throws Throwable {
				stack
						.push(Runtime.getAttribute(stack.pop(), key));
			}
		};
	}

	public static Instruction getIndex() {
		return new Instruction("getIndex") {
			public void exec(Context locals, OperandStack stack,
					InstructionPointer ip, State state)
					throws Throwable {
				Object key = stack.pop();
				stack.push(Runtime.getIndex(stack.pop(), key));
			}
		};
	}

	public static Instruction setAttr(final Object key) {
		return new Instruction("setAttr") {
			public void exec(Context locals, OperandStack stack,
					InstructionPointer ip, State state)
					throws Throwable {
				Object target = stack.pop();
				Object value = stack.pop();
				Runtime.setAttribute(target, key, value);
				stack.push(value);
			}
		};
	}

	public static Instruction setIndex() {
		return new Instruction("setIndex") {
			public void exec(Context locals, OperandStack stack,
					InstructionPointer ip, State state)
					throws Throwable {
				Object key = stack.pop();
				Object target = stack.pop();
				Object value = stack.pop();
				Runtime.setIndex(target, key, value);
				stack.push(value);
			}
		};
	}

	public static Instruction invokeMethod() {
		return new Instruction("invokeMethod") {
			public void exec(Context locals, OperandStack stack,
					InstructionPointer ip, State state)
					throws Throwable {
				Object methodKey = stack.pop();
				Object target = stack.pop();
				List args = (List) stack.pop();
				stack.push(Runtime.invokeNormalMethod(target, methodKey,
						args.toArray()));
			}
		};
	}

	public static Instruction invokeFunction() {
		return new Instruction("invokeFunction") {
			public void exec(Context locals, OperandStack stack,
					InstructionPointer ip, State state)
					throws Throwable {
				Object f = stack.pop();
				List args = (List) stack.pop();
				stack.push(Runtime.invokeFunction(f, args.toArray()));
			}
		};
	}

	public static Instruction ret() {
		return new Instruction("ret") {
			public void exec(Context locals, OperandStack stack,
					InstructionPointer ip, State state) {
				state.stop = true;
				stack.push(new FunctionReturn(stack.pop()));
			}
		};
	}

	public static Instruction invokeMethod(final String key,
			final boolean hasArgs) {
		return new Instruction("invokeMethod") {
			public void exec(Context locals, OperandStack stack,
					InstructionPointer ip, State state)
					throws Throwable {
				Object target = stack.pop();
				if (hasArgs)
					stack.push(Runtime.invokeSpecialMethod(target, key,
							((List) stack.pop()).toArray()));
				else
					stack.push(Runtime.invokeSpecialMethod(target, key));
			}
		};
	}

	public static GotoInstruction goToIfFalse() {
		return new GotoInstruction("goToIfFalse") {
			@Override
			public void exec(Context locals, OperandStack stack,
					InstructionPointer ip, State state) {
				Boolean val = (Boolean) stack.pop();
				if (!val)
					ip.p = getTarget();
			}
		};
	}

	public static GotoInstruction goTo(int ip) {
		return new GotoInstruction(ip);
	}

	public static GotoInstruction goTo() {
		return new GotoInstruction();
	}

	public static Instruction iterator(final String varName) {
		return new Instruction("iterator") {
			public void exec(Context locals, OperandStack stack,
					InstructionPointer ip, State state) {
				locals.put(varName, Runtime.getIterator(stack.pop()));
			}
		};
	}

	public static Instruction iteratorNext(final String varName,
			final String currentItemName) {
		return new Instruction("iteratorNext") {
			public void exec(Context locals, OperandStack stack,
					InstructionPointer ip, State state) {
				Iterator it = (Iterator) locals.get(varName);
				boolean hasNext = it.hasNext();
				if (hasNext)
					locals.put(currentItemName, it.next());
				stack.push(hasNext);
			}
		};
	}

	public static Instruction throwTop() {
		return new Instruction("throw") {
			public void exec(Context locals, OperandStack stack,
					InstructionPointer ip, State state)
					throws Throwable {
				throw Runtime.throwableObj(stack.pop());
			}
		};
	}

	public static Instruction yield() {
		return new Instruction("yield") {
			public void exec(Context locals, OperandStack stack,
					InstructionPointer ip, State state)
					throws Throwable {
				state.pause = true;
				stack.push(new FunctionReturn(stack.pop()));
			}
		};
	}

	public static Instruction jump() {
		return new Instruction("jump") {
			public void exec(Context locals, OperandStack stack,
					InstructionPointer ip, State state)
					throws Throwable {
				state.pause = true;
				Object arg = stack.pop();
				Object continuation = stack.pop();
				stack.push(new FunctionReturn(Runtime.jumpTo(
						continuation, arg)));
			}
		};
	}

}
