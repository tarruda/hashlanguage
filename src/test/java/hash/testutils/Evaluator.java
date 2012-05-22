package hash.testutils;

import static org.junit.Assert.fail;
import hash.lang.Context;
import hash.parsing.HashParser;
import hash.parsing.HashParser.program_return;
import hash.parsing.ParserFactory;
import hash.parsing.tree.HashNode;
import hash.parsing.visitors.evaluators.ProgramEvaluator;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.Tree;

public class Evaluator {

	public static Object eval(String code, Context context) {
		ProgramEvaluator target = new ProgramEvaluator(context);
		ANTLRStringStream source = new ANTLRStringStream(code);
		HashParser parser = ParserFactory.createParser(source);
		program_return psrReturn = null;
		try {
			psrReturn = parser.program();
			Tree t = (Tree) psrReturn.getTree();
			Tree r = target.visit(t);		
			if (r != null)
				return ((HashNode) r).getNodeData();
			return null;
		} catch (RecognitionException e) {
			e.printStackTrace();
			fail(e.getMessage());
			return null;
		}
	}

}
