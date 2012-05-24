package hash.parsing.visitors.simplevm;

import static hash.parsing.HashParser.ASSIGN;
import static hash.parsing.HashParser.ATTRIBUTE;
import static hash.parsing.HashParser.FOREACH;
import static hash.parsing.HashParser.FUNCTIONBLOCK;
import static hash.parsing.HashParser.INDEX;
import static hash.parsing.HashParser.NULL;
import static hash.parsing.HashParser.RETURN;
import hash.parsing.tree.HashNode;
import hash.parsing.visitors.LiteralEvaluator;
import hash.parsing.visitors.Result;
import hash.simplevm.Code;
import hash.simplevm.Instruction;
import hash.simplevm.Instructions;
import hash.simplevm.JumpInstruction;
import hash.util.Constants;
import hash.util.Err;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SimpleVmCompiler extends LiteralEvaluator {
	private static final String FOREACH_NESTING = "ForeachNesting";
	private static final String JUMPCONTINUE = "JumpContinue";
	private static final String JUMPBREAK = "JumpBreak";
	private static final String JUMPRETURN = "JumpReturn";

	private Code code;

	public SimpleVmCompiler() {
		code = new Code();
	}

	public Code getCode() {
		return code;
	}

	@Override
	protected HashNode visitForeach(HashNode node, HashNode id,
			HashNode iterable, HashNode action) {
		JumpInstruction jumpEnd = Instructions.jump();
		JumpInstruction jumpContinue = Instructions.jump();
		node.setNodeData(JUMPCONTINUE, jumpContinue);
		node.setNodeData(JUMPBREAK, jumpEnd);
		int nestingLevel = 0;
		HashNode current = node;
		while (current.getParent() != null && nestingLevel == 0) {
			// Find if this is a nested foreach. If so, the variable that will
			// store the iterator must be diferent than the one used by the
			// parent foreach. For disambiguating we append the nesting level
			// to the variable name
			current = (HashNode) current.getParent();
			if (current.getType() == FOREACH)
				nestingLevel = (Integer) current.getNodeData(FOREACH_NESTING) + 1;
		}
		node.setNodeData(FOREACH_NESTING, nestingLevel);
		String varName = "**iter**" + nestingLevel;
		int pointer = (Integer) visit(iterable).getNodeData();
		code.add(Instructions.iterator(varName));
		code.add(Instructions.iteratorNext(varName, id.getText()));
		int loopStart = code.size() - 1;
		JumpInstruction endIfFalse = Instructions.jumpIfFalse();
		code.add(endIfFalse);
		visit(action);
		code.add(jumpContinue);
		int endPointer = code.size();
		jumpEnd.setTarget(endPointer);
		endIfFalse.setTarget(endPointer);
		jumpContinue.setTarget(loopStart);
		return new Result(pointer);
	}

	@Override
	protected HashNode visitFor(HashNode node, HashNode init,
			HashNode condition, HashNode update, HashNode action) {
		JumpInstruction jumpEnd = Instructions.jump();
		JumpInstruction jumpContinue = Instructions.jump();
		node.setNodeData(JUMPCONTINUE, jumpContinue);
		node.setNodeData(JUMPBREAK, jumpEnd);
		int pointer = (Integer) visit(init).getNodeData();
		code.add(Instructions.pop());
		int loopStart = (Integer) visit(condition).getNodeData();
		code.add(Instructions.invokeMethod(Constants.BOOLEAN_VALUE, false));
		JumpInstruction endIfFalse = Instructions.jumpIfFalse();
		code.add(endIfFalse);
		visit(action);
		int loopContinue = (Integer) visit(update).getNodeData();
		code.add(Instructions.pop());
		code.add(Instructions.jump(loopStart));
		int endPointer = code.size();
		jumpEnd.setTarget(endPointer);
		endIfFalse.setTarget(endPointer);
		jumpContinue.setTarget(loopContinue);
		return new Result(pointer);
	}

	@Override
	protected HashNode visitWhile(HashNode node, HashNode condition,
			HashNode action) {
		JumpInstruction jumpBreak = Instructions.jump();
		JumpInstruction jumpContinue = Instructions.jump();
		node.setNodeData(JUMPCONTINUE, jumpContinue);
		node.setNodeData(JUMPBREAK, jumpBreak);
		int loopStart = (Integer) visit(condition).getNodeData();
		code.add(Instructions.invokeMethod(Constants.BOOLEAN_VALUE, false));
		JumpInstruction endIfFalse = Instructions.jumpIfFalse();
		code.add(endIfFalse);
		visit(action);
		code.add(jumpContinue);
		int endPointer = code.size();
		jumpBreak.setTarget(endPointer);
		endIfFalse.setTarget(endPointer);
		jumpContinue.setTarget(loopStart);
		return new Result(loopStart);
	}

	@Override
	protected HashNode visitDoWhile(HashNode node, HashNode condition,
			HashNode action) {
		JumpInstruction jumpEnd = Instructions.jump();
		JumpInstruction jumpContinue = Instructions.jump();
		node.setNodeData(JUMPCONTINUE, jumpContinue);
		node.setNodeData(JUMPBREAK, jumpEnd);
		int loopStart = (Integer) visit(action).getNodeData();
		int loopContinue = (Integer) visit(condition).getNodeData();
		code.add(Instructions.invokeMethod(Constants.BOOLEAN_VALUE, false));
		JumpInstruction endIfFalse = Instructions.jumpIfFalse();
		code.add(endIfFalse);
		code.add(Instructions.jump(loopStart));
		int endPointer = code.size();
		jumpEnd.setTarget(endPointer);
		endIfFalse.setTarget(endPointer);
		jumpContinue.setTarget(loopContinue);
		return new Result(loopStart);
	}

	@Override
	protected HashNode visitIf(HashNode node, HashNode condition,
			HashNode trueAction, HashNode falseAction) {
		int pointer = (Integer) visit(condition).getNodeData();
		code.add(Instructions.invokeMethod(Constants.BOOLEAN_VALUE, false));
		JumpInstruction jumpElseEnd = Instructions.jumpIfFalse();
		code.add(jumpElseEnd);
		visit(trueAction);
		if (falseAction.getType() != NULL) {
			JumpInstruction jumpEnd = Instructions.jump();
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
		Map<HashNode, JumpInstruction> returnJumps = getReturnJumps(node);
		code.add(returnJumps.get(node));
		return new Result(code.size() - 1);
	}

	@Override
	protected HashNode visitSwitch(HashNode node, HashNode continuation,
			HashNode arg) {
		int pointer = (Integer) visit(continuation).getNodeData();
		visit(arg);
		code.add(Instructions.resume());
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
			if (current.contains(JUMPBREAK)) {
				code.add((Instruction) current.getNodeData(JUMPBREAK));
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
			if (current.contains(JUMPCONTINUE)) {
				code.add((Instruction) current.getNodeData(JUMPCONTINUE));
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
				if (child.getType() == ASSIGN)// || child.getType() ==
												// INVOCATION)
					code.add(Instructions.pop());
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
		JumpInstruction tryCatchStart = Instructions.jump();
		code.add(tryCatchStart);
		int pointer = code.size() - 1;
		JumpInstruction tryCatchEnd = Instructions.jump();
		JumpInstruction finallyStart = null;
		if (finallyBlock.getType() != NULL) {
			Map<HashNode, JumpInstruction> finallyReturnJumps = new HashMap<HashNode, JumpInstruction>();
			Map<HashNode, JumpInstruction> returnJumps = getReturnJumps(node);
			if (returnJumps != null) {
				for (HashNode returnStmt : returnJumps.keySet()) {
					// inline the finally block for each return statement
					// these specialized copies will jump to the corresponding
					// return statement after doing its job
					finallyStart = Instructions.jump((Integer) visit(
							finallyBlock).getNodeData());
					// jump to the actual return statement
					code.add(returnJumps.get(returnStmt));
					finallyReturnJumps.put(returnStmt, finallyStart);
				}
				replaceReturnJumps(node, finallyReturnJumps);
			}
			// inline the finally block that would normally execute if no
			// flow control statements are executed
			finallyStart = Instructions.jump((Integer) visit(finallyBlock)
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
			code.add(finallyStart);
			code.addTryCatchBlock(tryStart, tryEnd, catchPointer,
					exceptionTypeId);
		}
		tryCatchEnd.setTarget(code.size());
		if (finallyStart != null)
			code.addTryCatchBlock(tryStart, tryEnd, catchAllPointer, null);

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
		SimpleVmCompiler compiler = new SimpleVmCompiler();
		setupReturns(block, compiler);
		if (returnsContinuation)
			code.add(Instructions.pushContinuationFactory(params,
					compiler.code, isMethod));
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

	private void replaceReturnJumps(HashNode node,
			Map<HashNode, JumpInstruction> value) {
		HashNode current = node;
		while (current.getParent() != null) {
			current = (HashNode) current.getParent();
			if (current.contains(JUMPRETURN)) {
				current.setNodeData(JUMPRETURN, value);
			}
		}
	}

	private Map<HashNode, JumpInstruction> getReturnJumps(HashNode node) {
		HashNode current = node;
		while (current.getParent() != null) {
			current = (HashNode) current.getParent();
			if (current.contains(JUMPRETURN)) {
				return (Map<HashNode, JumpInstruction>) current
						.getNodeData(JUMPRETURN);
			}
		}
		return null;
	}

	private void setupReturns(HashNode block, SimpleVmCompiler compiler) {
		List<HashNode> returnStatements = new ArrayList<HashNode>();
		int blockLen = block.getChildCount();
		for (int i = 0; i < blockLen; i++)
			collectAllReturnStatements(returnStatements,
					(HashNode) block.getChild(i));
		JumpInstruction functionStart = Instructions.jump();
		HashMap<HashNode, JumpInstruction> jumpsToReturnStatements = new HashMap<HashNode, JumpInstruction>();
		compiler.code.add(functionStart);
		for (HashNode hashNode : returnStatements) {
			int pointer = (Integer) compiler.visit(
					(HashNode) hashNode.getChild(0)).getNodeData();
			compiler.code.add(Instructions.ret());
			jumpsToReturnStatements.put(hashNode, Instructions.jump(pointer));
		}
		block.setNodeData(JUMPRETURN, jumpsToReturnStatements);
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
