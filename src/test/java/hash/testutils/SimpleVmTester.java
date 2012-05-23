package hash.testutils;

import static org.junit.Assert.fail;
import hash.lang.Context;
import hash.parsing.HashParser;
import hash.parsing.HashParser.program_return;
import hash.parsing.ParserFactory;
import hash.parsing.tree.HashNode;
import hash.parsing.visitors.simplevm.SimpleVmCompiler;
import hash.simplevm.Code;
import hash.simplevm.SimpleVm;

import org.antlr.runtime.ANTLRStringStream;

public class SimpleVmTester {

	public static Object eval(String code, Context context) {
		return eval(code, context, null);
	}

	public static Object eval(String code, Context context, Class exceptionClass) {
		SimpleVmCompiler compiler = new SimpleVmCompiler();
		ANTLRStringStream source = new ANTLRStringStream(code);
		HashParser parser = ParserFactory.createParser(source);
		program_return psrReturn = null;
		try {
			psrReturn = parser.program();
			HashNode t = (HashNode) psrReturn.getTree();
			compiler.visit(t);
			Code c = compiler.getCode();
			return SimpleVm
					.execute(c.toArray(), c.getTryCatchBlocks(), context);
		} catch (Throwable e) {
			if (exceptionClass != null && exceptionClass != e.getClass())
				fail(e.getMessage());
			return null;
		}
	}
}
