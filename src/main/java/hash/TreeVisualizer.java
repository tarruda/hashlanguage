package hash;

import hash.parsing.ConcreteHashLexer;
import hash.parsing.ConcreteHashParser;
import hash.parsing.HashParser.program_return;

import java.io.FileWriter;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.DOTTreeGenerator;
import org.antlr.runtime.tree.Tree;
import org.antlr.stringtemplate.StringTemplate;

class TreeVisualizer {

	public static void main(String[] args) throws Exception {
		ANTLRStringStream source = new ANTLRStringStream(
				"f = (n) { x=1; import test}");
		ConcreteHashLexer lexer = new ConcreteHashLexer(source);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		ConcreteHashParser parser = new ConcreteHashParser(tokens);
		program_return psrReturn = parser.program();
		Tree t = (Tree) psrReturn.getTree();
		DOTTreeGenerator gen = new DOTTreeGenerator();
		String outputName = "target/source.dot";
		System.out.println("    Producing AST dot (graphviz) file");
		StringTemplate st = gen.toDOT(t, new CommonTreeAdaptor());
		FileWriter outputStream = new FileWriter(outputName);
		outputStream.write(st.toString());
		outputStream.close();
	}

}
