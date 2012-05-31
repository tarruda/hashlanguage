package org.hashlang.testutils;

import static org.junit.Assert.fail;

import org.antlr.runtime.ANTLRStringStream;
import org.hashlang.parsing.HashParser;
import org.hashlang.parsing.HashParser.program_return;
import org.hashlang.parsing.ParserFactory;
import org.hashlang.parsing.tree.HashNode;
import org.hashlang.runtime.AppRuntime;
import org.hashlang.runtime.Context;
import org.hashlang.simplevm.Code;
import org.hashlang.simplevm.Compiler;
import org.hashlang.simplevm.SimpleVm;

public class SimpleVmTester {

	public static Object eval(AppRuntime runtime, String code, Context context) {
		return eval(runtime, code, context, null);
	}

	public static Object eval(AppRuntime runtime, String code, Context context,
			Class exceptionClass) {
		Throwable ex = null;
		Compiler compiler = new Compiler();
		ANTLRStringStream source = new ANTLRStringStream(code);
		HashParser parser = ParserFactory.createParser(source);
		program_return psrReturn = null;
		try {
			psrReturn = parser.program();
			HashNode t = (HashNode) psrReturn.getTree();
			compiler.visit(t);
			Code c = compiler.getCode();
			SimpleVm.execute(runtime, c.getInstructions(),
					c.getTryCatchBlocks(), context);
		} catch (Throwable e) {
			ex = e;
		}
		if (exceptionClass != null) {
			if (ex == null)
				fail("Expecting exception");
			else if (!exceptionClass.isAssignableFrom(ex.getClass()))
				fail(String
						.format("Incompatible exception type, expecting '%s', caught '%s'",
								exceptionClass.getCanonicalName(), ex
										.getClass().getCanonicalName()));
		} else if (ex != null)
			throw new RuntimeException(ex);
		return context.restore();
	}
}
