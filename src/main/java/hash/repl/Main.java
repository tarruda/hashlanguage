package hash.repl;

import hash.runtime.Context;
import hash.runtime.Factory;

import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException {
		Context locals = Factory.createContext();
		REPLInterpreter interpreter = new REPLInterpreter(System.in,
				System.out, System.err);
		System.out.print("Hash REPL interpreter");
		while (true) {			
			System.out.print("\n#> ");
			interpreter.rep(locals);			
		}
	}
}
