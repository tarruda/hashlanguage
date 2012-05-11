package hash;

import hash.parsing.HashLexer;
import hash.parsing.HashParser;
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
		//ANTLRStringStream source = new ANTLRStringStream("3+4+2+5+3*1;");
		//ANTLRStringStream source = new ANTLRStringStream("5.toString(2,3,4)['test'].abc;");
		ANTLRStringStream source = new ANTLRStringStream("x.t['34'].number += 4+4*2;");	
		HashLexer lexer = new HashLexer(source);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		HashParser parser = new HashParser(tokens);
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
