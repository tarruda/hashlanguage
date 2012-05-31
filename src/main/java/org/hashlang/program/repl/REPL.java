package org.hashlang.program.repl;


import java.io.IOException;

import org.hashlang.runtime.AppRuntime;

public class REPL {

	public static void start(AppRuntime runtime) throws IOException {
		REPLInterpreter interpreter = new REPLInterpreter(runtime);
		System.out.print("Hash REPL interpreter");
		while (true) {
			System.out.println();
			System.out.print("#>> ");
			interpreter.rep(runtime.getMain());
		}
	}
}
