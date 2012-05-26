package hash.repl;

import hash.parsing.HashParser;
import hash.parsing.ParserFactory;
import hash.parsing.tree.HashNode;
import hash.runtime.Context;
import hash.simplevm.Code;
import hash.simplevm.Compiler;
import hash.simplevm.SimpleVm;

import java.io.InputStream;
import java.io.PrintStream;

public class REPLInterpreter {

	private PrintStream out;
	private PrintStream err;
	private REPLTokenSource tokenSource;

	public REPLInterpreter(InputStream in, PrintStream out, PrintStream err) {
		this.out = out;
		this.err = err;
		this.tokenSource = new REPLTokenSource(in);
	}

	public void rep(Context locals) {
		HashParser parser = ParserFactory.createParser(tokenSource);
		HashNode tree = null;
		try {
			tree = (HashNode) parser.program().getTree();
		} catch (Exception e) {
			e.printStackTrace(err);
		}
		Compiler c = new Compiler();
		c.visit(tree);
		Code code = c.getCode();
		try {
			SimpleVm.execute(code.getInstructions(), code.getTryCatchBlocks(),
					locals);
			out.print(locals.restore());
		} catch (Throwable e) {
			e.printStackTrace(err);
		}
	}

}
