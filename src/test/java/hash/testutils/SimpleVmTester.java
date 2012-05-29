package hash.testutils;

import static org.junit.Assert.fail;
import hash.parsing.HashParser;
import hash.parsing.HashParser.program_return;
import hash.parsing.ParserFactory;
import hash.parsing.tree.HashNode;
import hash.runtime.AppRuntime;
import hash.runtime.Context;
import hash.simplevm.Code;
import hash.simplevm.Compiler;
import hash.simplevm.SimpleVm;

import org.antlr.runtime.ANTLRStringStream;

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
