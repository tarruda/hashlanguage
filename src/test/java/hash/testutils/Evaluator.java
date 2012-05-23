package hash.testutils;

import static org.junit.Assert.fail;
import hash.lang.Context;
import hash.parsing.HashParser;
import hash.parsing.HashParser.literal_return;
import hash.parsing.ParserFactory;
import hash.parsing.tree.HashNode;
import hash.parsing.visitors.LiteralEvaluator;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.RecognitionException;

public class Evaluator {

	public static Object eval(String code, Context context) {
		LiteralEvaluator target = new LiteralEvaluator();
		ANTLRStringStream source = new ANTLRStringStream(code);
		HashParser parser = ParserFactory.createParser(source);
		literal_return psrReturn = null;
		try {
			psrReturn = parser.literal();
			HashNode t = (HashNode) psrReturn.getTree();
			HashNode r = target.visit(t);
			if (r != null)
				return r.getNodeData();
			return null;
		} catch (RecognitionException e) {
			e.printStackTrace();
			fail(e.getMessage());
			return null;
		}
	}

}
