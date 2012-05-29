package hash.program.repl;

import hash.parsing.HashParser;
import hash.parsing.ParserFactory;
import hash.parsing.tree.HashNode;
import hash.runtime.AppRuntime;
import hash.runtime.Context;
import hash.simplevm.Code;
import hash.simplevm.Compiler;
import hash.simplevm.SimpleVm;

import java.io.PrintStream;

public class REPLInterpreter {

	private AppRuntime runtime;
	private PrintStream out;
	private PrintStream err;
	private REPLTokenSource tokenSource;

	public REPLInterpreter(AppRuntime runtime) {
		this.runtime = runtime;
		this.out = runtime.getStdout();
		this.err = runtime.getStderr();
		this.tokenSource = new REPLTokenSource(runtime.getStdin(), out);
	}

	public void rep(Context locals) {
		HashParser parser = ParserFactory.createParser(tokenSource);
		parser.setErr(err);
		HashNode tree = null;
		Code code = null;
		try {
			tree = (HashNode) parser.program().getTree();
			Compiler c = new Compiler();
			c.visit(tree);
			code = c.getCode();
		} catch (Exception e) {
			e.printStackTrace(err);
			return;
		}
		try {
			SimpleVm.execute(runtime, code.getInstructions(),
					code.getTryCatchBlocks(), locals);
			out.print(locals.restore());
		} catch (Throwable e) {
			e.printStackTrace(err);
		}
	}

}
