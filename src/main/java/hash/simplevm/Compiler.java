package hash.simplevm;

import static hash.parsing.HashParser.ASSIGN;
import static hash.parsing.HashParser.ATTRIBUTE;
import static hash.parsing.HashParser.BINARY;
import static hash.parsing.HashParser.BOOLEAN;
import static hash.parsing.HashParser.CONDITIONAL;
import static hash.parsing.HashParser.FLOAT;
import static hash.parsing.HashParser.FUNCTIONBLOCK;
import static hash.parsing.HashParser.INCR;
import static hash.parsing.HashParser.INDEX;
import static hash.parsing.HashParser.INTEGER;
import static hash.parsing.HashParser.INVOCATION;
import static hash.parsing.HashParser.JUMPTO;
import static hash.parsing.HashParser.LIST;
import static hash.parsing.HashParser.MAP;
import static hash.parsing.HashParser.NAMEREF;
import static hash.parsing.HashParser.NULL;
import static hash.parsing.HashParser.REGEX;
import static hash.parsing.HashParser.RETURN;
import static hash.parsing.HashParser.SLICE;
import static hash.parsing.HashParser.STRING;
import static hash.parsing.HashParser.THIS;
import static hash.parsing.HashParser.UNARY;
import static hash.parsing.HashParser.UNPACK_ASSIGN;
import static hash.parsing.HashParser.YIELD;
import hash.parsing.tree.HashNode;
import hash.parsing.tree.RuntimeInvocation;
import hash.parsing.visitors.LiteralEvaluator;
import hash.parsing.visitors.Result;
import hash.util.Constants;
import hash.util.Err;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Compiler extends LiteralEvaluator {

	private static final String GOTOCONTINUE = "GoToContinue";
	private static final String GOTOBREAK = "GoToBreak";
	private static final String GOTORETURN = "GoToReturn";

	private Code code;
	private boolean function;

	public Compiler() {
		this(false);
	}

	public Compiler(boolean function) {
		code = new Code();
		this.function = function;
	}

	public Code getCode() {
		return code;
	}

	@Override
	protected HashNode visitLoop(HashNode node, HashNode init,
			HashNode condition, HashNode update, HashNode action) {
		GotoInstruction gotoBreak = Instructions.goTo();
		GotoInstruction gotoContinue = Instructions.goTo();
		node.setNodeData(GOTOCONTINUE, gotoContinue);
		node.setNodeData(GOTOBREAK, gotoBreak);
		int pointer = code.size();
		if (init != null) 
			pointer = (Integer) visit(init).getNodeData();			
		int loopStart = (Integer) visit(condition).getNodeData();
		code.add(Instructions.invokeMethod(Constants.BOOLEAN_VALUE, false));
		GotoInstruction endIfFalse = Instructions.goToIfFalse();
		code.add(endIfFalse);
		visit(action);
		int loopContinue = code.size();
		if (update != null)
			loopContinue = (Integer) visit(update).getNodeData();		
		code.add(Instructions.goTo(loopStart));
		int endPointer = code.size();
		gotoBreak.setTarget(endPointer);
		endIfFalse.setTarget(endPointer);
		gotoContinue.setTarget(loopContinue);
		return new Result(pointer);
	}

	@Override
	protected HashNode visitIf(HashNode node, HashNode condition,
			HashNode trueAction, HashNode falseAction) {
		int pointer = (Integer) visit(condition).getNodeData();
		code.add(Instructions.invokeMethod(Constants.BOOLEAN_VALUE, false));
		GotoInstruction jumpElseEnd = Instructions.goToIfFalse();
		code.add(jumpElseEnd);
		visit(trueAction);
		if (falseAction.getType() != NULL) {
			GotoInstruction jumpEnd = Instructions.goTo();
			code.add(jumpEnd);
			int elsePointer = (Integer) visit(falseAction).getNodeData();
			jumpElseEnd.setTarget(elsePointer);
			jumpEnd.setTarget(code.size());
		} else
			jumpElseEnd.setTarget(code.size());
		return new Result(pointer);
	}

	@Override
	protected HashNode visitReturn(HashNode node, HashNode returnExpression) {
		Map<HashNode, GotoInstruction> returnJumps = getReturnJumps(node);
		code.add(returnJumps.get(node));
		return new Result(code.size() - 1);
	}

	@Override
	protected HashNode visitJump(HashNode node, HashNode continuation,
			HashNode arg) {
		int pointer = (Integer) visit(continuation).getNodeData();
		visit(arg);
		code.add(Instructions.jump());
		return new Result(pointer);
	}

	@Override
	protected HashNode visitYield(HashNode node, HashNode yieldExpression) {
		int pointer = (Integer) visit(yieldExpression).getNodeData();
		code.add(Instructions.yield());
		return new Result(pointer);
	}

	@Override
	protected HashNode visitBreak(HashNode node) {
		HashNode current = node;
		while (current.getParent() != null) {
			current = (HashNode) current.getParent();
			if (current.contains(GOTOBREAK)) {
				code.add((Instruction) current.getNodeData(GOTOBREAK));
				break;
			}
		}
		return new Result(code.size() - 1);
	}

	@Override
	protected HashNode visitContinue(HashNode node) {
		HashNode current = node;
		while (current.getParent() != null) {
			current = (HashNode) current.getParent();
			if (current.contains(GOTOCONTINUE)) {
				code.add((Instruction) current.getNodeData(GOTOCONTINUE));
				break;
			}
		}
		return new Result(code.size() - 1);
	}

	@Override
	protected HashNode visitBlock(HashNode node) {
		HashNode p = null;
		try {
			int len = node.getChildCount();
			int pointer = -1;
			for (int i = 0; i < len; i++) {
				HashNode child = (HashNode) node.getChild(i);
				p = visit(child);
				if (pointer == -1)
					pointer = (Integer) p.getNodeData();
				switch (child.getType()) {
				case ASSIGN:
				case INVOCATION:
				case CONDITIONAL:
				case BINARY:
				case UNARY:
				case YIELD:
				case JUMPTO:
				case INCR:
				case ATTRIBUTE:
				case UNPACK_ASSIGN:
				case INDEX:
				case SLICE:
				case MAP:
				case LIST:
				case NAMEREF:
				case THIS:
				case REGEX:
				case STRING:
				case FLOAT:
				case INTEGER:
				case BOOLEAN:
				case NULL:
					if (function)
						code.add(Instructions.pop());
					else
						// if not inside a function, store the last expression
						// result
						code.add(Instructions.save());
				}
			}
			return new Result(pointer);
		} catch (Throwable ex) {
			throw Err.ex(ex);
		}
	}

	@Override
	protected HashNode visitAssignment(HashNode node, HashNode target,
			HashNode expression) {
		int pointer = (Integer) visit(expression).getNodeData();// push value
		if (target.getType() == ATTRIBUTE || target.getType() == INDEX) {
			visit((HashNode) target.getChild(0)); // push object
			if (target.getType() == ATTRIBUTE)
				code.add(Instructions.setAttr(target.getChild(1).getText()));
			else {
				visit((HashNode) target.getChild(1)); // push key
				code.add(Instructions.setIndex());
			}
		} else {
			// target is an identifier
			int l = 0;
			Object level = target.getNodeData(HashNode.CONTEXT_LEVEL);
			if (level != null)
				l = (Integer) level;
			code.add(Instructions.setNameRef(target.getText(), l));
		}
		return new Result(pointer);
	}

	@Override
	protected HashNode visitEvalAndIncrement(HashNode node, HashNode target,
			HashNode assignment) {
		int pointer = (Integer) visit(target).getNodeData();
		visit(assignment);
		code.add(Instructions.pop());
		return new Result(pointer);
	}

	@Override
	protected HashNode visitThrow(HashNode node, HashNode throwableExpression) {
		int pointer = (Integer) visit(throwableExpression).getNodeData();
		code.add(Instructions.throwTop());
		return new Result(pointer);
	}

	@Override
	protected HashNode visitTryStatement(HashNode node, HashNode tryBlock,
			HashNode catchBlocks, HashNode finallyBlock) {
		int catchAllPointer = -1;
		GotoInstruction tryCatchStart = Instructions.goTo();
		code.add(tryCatchStart);
		int pointer = code.size() - 1;
		GotoInstruction tryCatchEnd = Instructions.goTo();
		GotoInstruction finallyStart = null;
		if (finallyBlock.getType() != NULL) {
			Map<HashNode, GotoInstruction> finallyReturnJumps = new HashMap<HashNode, GotoInstruction>();
			Map<HashNode, GotoInstruction> returnJumps = getReturnJumps(node);
			if (returnJumps != null) {
				for (HashNode returnStmt : returnJumps.keySet()) {
					// inline the finally block for each return statement
					// these specialized copies will jump to the corresponding
					// return statement after doing its job
					finallyStart = Instructions.goTo((Integer) visit(
							finallyBlock).getNodeData());
					// jump to the actual return statement
					code.add(returnJumps.get(returnStmt));
					finallyReturnJumps.put(returnStmt, finallyStart);
				}
				replaceReturnJumps(node, finallyReturnJumps);
			}
			// inline the finally block that would normally execute if no
			// flow control statements are executed
			finallyStart = Instructions.goTo((Integer) visit(finallyBlock)
					.getNodeData());
			code.add(tryCatchEnd);
			// in the case exception is thrown and not catched, a catch-all
			// block that will jump to another copy of the
			// finally block, which will retrow the exception at the end
			String n = "**finally**";
			code.add(Instructions.setNameRef(n, 0));
			catchAllPointer = code.size() - 1;
			visit(finallyBlock);
			code.add(Instructions.getNameRef(n, 0));
			code.add(Instructions.throwTop());
		}
		int tryStart = (Integer) visit(tryBlock).getNodeData();
		if (finallyStart != null)
			code.add(finallyStart);
		tryCatchStart.setTarget(tryStart);
		int tryEnd = code.size() - 1;
		int catchBlocksLen = catchBlocks.getChildCount();
		for (int i = 0; i < catchBlocksLen; i++) {
			HashNode catchBlock = (HashNode) catchBlocks.getChild(i);
			String exceptionTypeId = catchBlock.getChild(0).getText();
			if (exceptionTypeId.equals("null"))
				exceptionTypeId = null;
			String id = catchBlock.getChild(1).getText();
			code.add(Instructions.setNameRef(id, 0));
			int catchPointer = code.size() - 1;
			code.add(Instructions.pop());
			visit((HashNode) catchBlock.getChild(2));
			if (finallyStart != null)
				code.add(finallyStart);
			else
				code.add(tryCatchEnd);
			code.addTryCatchBlock(tryStart, tryEnd, catchPointer,
					exceptionTypeId);
		}
		tryCatchEnd.setTarget(code.size());
		if (finallyStart != null)
			code.addTryCatchBlock(tryStart, tryEnd, catchAllPointer, null);

		return new Result(pointer);
	}

	@Override
	protected HashNode visitConditionalExpression(HashNode node,
			HashNode condition, HashNode trueValue, HashNode falseValue) {
		int pointer = (Integer) visit(condition).getNodeData();
		code.add(Instructions.invokeMethod(Constants.BOOLEAN_VALUE, false));
		GotoInstruction gotoFalse = Instructions.goToIfFalse();
		GotoInstruction gotoEnd = Instructions.goTo();
		code.add(gotoFalse);
		visit(trueValue);
		code.add(gotoEnd);
		gotoFalse.setTarget((Integer) visit(falseValue).getNodeData());
		gotoEnd.setTarget(code.size());
		return new Result(pointer);
	}

	@Override
	protected HashNode visitBinaryExpression(HashNode node, HashNode left,
			HashNode right) {
		int pointer = (Integer) visit(left).getNodeData();
		visit(right);
		String operatorTxt = node.getText();
		if (operatorTxt.equals("is")) // skip the runtime
			code.add(Instructions.areSame());
		else
			code.add(Instructions.invokeBinary(node.getText()));
		return new Result(pointer);
	}

	@Override
	protected HashNode visitUnaryExpression(HashNode node, HashNode operand) {
		int pointer = (Integer) visit(operand).getNodeData();
		String operatorTxt = node.getText();
		if (!operatorTxt.equals("+"))
			code.add(Instructions.invokeUnary(node.getText()));
		return new Result(pointer);
	}

	@Override
	protected HashNode visitNameReference(HashNode node) {
		int l = 0;
		Object level = node.getNodeData(HashNode.CONTEXT_LEVEL);
		if (level != null)
			l = (Integer) level;
		code.add(Instructions.getNameRef(node.getText(), l));
		return new Result(code.size() - 1);
	}

	@Override
	protected HashNode visitAttributeAccess(HashNode node, HashNode target,
			HashNode attributeKey) {
		int pointer = (Integer) visit(target).getNodeData();
		code.add(Instructions.getAttr(attributeKey.getText()));
		return new Result(pointer);
	}

	@Override
	protected HashNode visitIndexAccess(HashNode node, HashNode target,
			HashNode itemKey) {
		int pointer = (Integer) visit(target).getNodeData();
		visit(itemKey);
		code.add(Instructions.getIndex());
		return new Result(pointer);
	}

	@Override
	protected HashNode visitSlice(HashNode node, HashNode target,
			HashNode sliceArgs) {
		int pointer = (Integer) visit(target).getNodeData();
		visit(sliceArgs);
		code.add(Instructions.pushSlice());
		return new Result(pointer);
	}

	@Override
	protected HashNode visitFunction(HashNode node, HashNode parameters,
			HashNode block) {
		boolean isMethod = node.getNodeData(HashNode.IS_METHOD) == Boolean.TRUE;
		boolean returnsContinuation = node
				.getNodeData(HashNode.RETURNS_CONTINUATION) == Boolean.TRUE;
		int len = parameters.getChildCount();
		List params = new ArrayList(len);
		for (int i = 0; i < len; i++)
			params.add(parameters.getChild(i).getText());
		Compiler compiler = new Compiler(true);
		setupReturns(block, compiler);
		if (returnsContinuation)
			code.add(Instructions.pushTrampolineFactory(params, compiler.code,
					isMethod));
		else
			code.add(Instructions.pushFunction(params, compiler.code, isMethod));
		return new Result(code.size() - 1);
	}

	@Override
	protected HashNode visitInvocation(HashNode node, HashNode target,
			HashNode args) {
		int pointer = (Integer) visit(args).getNodeData();
		if (target.getType() == ATTRIBUTE || target.getType() == INDEX) {
			// this is a method call
			visit((HashNode) target.getChild(0));
			visit((HashNode) target.getChild(1));
			code.add(Instructions.invokeMethod());
		} else {
			// normal function call
			visit(target);
			code.add(Instructions.invokeFunction());
		}
		return new Result(pointer);
	}

	@Override
	protected HashNode visitRuntimeInvocation(RuntimeInvocation node) {
		HashNode[] args = node.getArgs();
		int pointer = -1;
		for (HashNode arg : args) {
			if (pointer == -1)
				pointer = (Integer) visit(arg).getNodeData();
			else
				visit(arg);
		}
		code.add(Instructions.runtimeInvoke(node.getRuntimeMethod()));
		return new Result(pointer);
	}

	@Override
	protected HashNode visitMap(HashNode node) {
		int len = node.getChildCount();
		int pointer = -1;
		for (int i = 0; i < len; i++) {
			if (pointer == -1)
				pointer = (Integer) visit((HashNode) node.getChild(i))
						.getNodeData();
			else
				visit((HashNode) node.getChild(i)).getNodeData();
			visit((HashNode) node.getChild(i).getChild(0));
		}
		code.add(Instructions.pushMap(len));
		if (len == 0)
			pointer = code.size() - 1;
		return new Result(pointer);
	}

	@Override
	protected HashNode visitList(HashNode node) {
		int len = node.getChildCount();
		int pointer = -1;
		for (int i = len - 1; i >= 0; i--)
			if (pointer == -1)
				pointer = (Integer) visit((HashNode) node.getChild(i))
						.getNodeData();
			else
				visit((HashNode) node.getChild(i));
		code.add(Instructions.pushList(len));
		if (len == 0)
			pointer = code.size() - 1;
		return new Result(pointer);
	}

	@Override
	protected HashNode visitRegex(HashNode node) {
		String regexLiteral = node.getText();
		String regexText = regexLiteral.substring(1,
				regexLiteral.lastIndexOf('/'));
		int flags = 0;
		if (regexLiteral.endsWith("i"))
			flags = Pattern.CASE_INSENSITIVE;
		code.add(Instructions.push(Pattern.compile(regexText, flags)));
		return new Result(code.size() - 1);
	}

	@Override
	protected HashNode visitThis(HashNode node) {
		return visitNameReference(node);
	}

	@Override
	protected HashNode visitString(HashNode node) {
		code.add(Instructions.push(super.visitString(node).getNodeData()));
		return new Result(code.size() - 1);
	}

	@Override
	protected HashNode visitInteger(HashNode node) {
		code.add(Instructions.push(super.visitInteger(node).getNodeData()));
		return new Result(code.size() - 1);
	}

	@Override
	protected HashNode visitFloat(HashNode node) {
		code.add(Instructions.push(super.visitFloat(node).getNodeData()));
		return new Result(code.size() - 1);
	}

	@Override
	protected HashNode visitBoolean(HashNode node) {
		code.add(Instructions.push(super.visitBoolean(node).getNodeData()));
		return new Result(code.size() - 1);
	}

	@Override
	protected HashNode visitNull(HashNode node) {
		code.add(Instructions.push(super.visitNull(node).getNodeData()));
		return new Result(code.size() - 1);
	}

	@Override
	protected void pop() {
		code.add(Instructions.pop());
	}

	private void replaceReturnJumps(HashNode node,
			Map<HashNode, GotoInstruction> value) {
		HashNode current = node;
		while (current.getParent() != null) {
			current = (HashNode) current.getParent();
			if (current.contains(GOTORETURN)) {
				current.setNodeData(GOTORETURN, value);
			}
		}
	}

	private Map<HashNode, GotoInstruction> getReturnJumps(HashNode node) {
		HashNode current = node;
		while (current.getParent() != null) {
			current = (HashNode) current.getParent();
			if (current.contains(GOTORETURN)) {
				return (Map<HashNode, GotoInstruction>) current
						.getNodeData(GOTORETURN);
			}
		}
		return null;
	}

	private void setupReturns(HashNode block, Compiler compiler) {
		List<HashNode> returnStatements = new ArrayList<HashNode>();
		int blockLen = block.getChildCount();
		for (int i = 0; i < blockLen; i++)
			collectAllReturnStatements(returnStatements,
					(HashNode) block.getChild(i));
		GotoInstruction functionStart = Instructions.goTo();
		HashMap<HashNode, GotoInstruction> jumpsToReturnStatements = new HashMap<HashNode, GotoInstruction>();
		compiler.code.add(functionStart);
		for (HashNode hashNode : returnStatements) {
			int pointer = (Integer) compiler.visit(
					(HashNode) hashNode.getChild(0)).getNodeData();
			compiler.code.add(Instructions.ret());
			jumpsToReturnStatements.put(hashNode, Instructions.goTo(pointer));
		}
		block.setNodeData(GOTORETURN, jumpsToReturnStatements);
		functionStart.setTarget((Integer) compiler.visit(block).getNodeData());
	}

	private void collectAllReturnStatements(List<HashNode> returnStatements,
			HashNode current) {
		if (current.getType() == FUNCTIONBLOCK)
			return;
		if (current.getType() == RETURN)
			returnStatements.add(current);
		else {
			int len = current.getChildCount();
			for (int i = 0; i < len; i++)
				collectAllReturnStatements(returnStatements,
						(HashNode) current.getChild(i));
		}
	}

}
