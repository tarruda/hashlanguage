package org.hashlang;


import java.io.FileWriter;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.DOTTreeGenerator;
import org.antlr.runtime.tree.Tree;
import org.antlr.stringtemplate.StringTemplate;
import org.hashlang.parsing.HashParser;
import org.hashlang.parsing.HashParser.program_return;
import org.hashlang.parsing.ParserFactory;

class TreeVisualizer {

	public static void main(String[] args) throws Exception {
		ANTLRStringStream source = new ANTLRStringStream("try{\n" + 
				"  print()\n" + 
				"}\n" + 
				"catch (IllegalArgException iex) {\n" + 
				"  print2()\n" + 
				"}\n" + 
				"catch(RuntimeException rex) {\n" + 
				"  print3()\n" + 
				"  ok()\n" + 
				"} \n" + 
				"catch (ex){\n" + 
				"  //print()\n" + 
				"  return 4\n" + 
				"} \n" + 
				"finally{\n" + 
				"  ok() \n" + 
				"  return call2()" + 
				"}");
		HashParser parser = ParserFactory.createParser(source);
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
