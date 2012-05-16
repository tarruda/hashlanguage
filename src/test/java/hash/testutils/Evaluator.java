package hash.testutils;

import static org.junit.Assert.fail;
import hash.lang.Context;
import hash.parsing.ConcreteHashLexer;
import hash.parsing.ConcreteHashParser;
import hash.parsing.HashParser.program_return;
import hash.parsing.tree.Result;
import hash.parsing.visitors.evaluators.ProgramEvaluator;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.Tree;

public class Evaluator {

	public static Object eval(String code, Context context) {
		ProgramEvaluator target = new ProgramEvaluator(context);
		ConcreteHashLexer lexer = new ConcreteHashLexer();
		ANTLRStringStream source = new ANTLRStringStream(code);
		lexer.setCharStream(source);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		ConcreteHashParser parser = new ConcreteHashParser(tokens);
		program_return psrReturn = null;
		try {
			psrReturn = parser.program();
			Tree t = (Tree) psrReturn.getTree();
			return ((Result) target.visit(t)).getNodeData();
		} catch (RecognitionException e) {
			e.printStackTrace();
			fail(e.getMessage());
			return null;
		}
	}

}
