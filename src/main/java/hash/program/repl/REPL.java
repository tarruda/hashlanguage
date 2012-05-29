package hash.program.repl;

import hash.runtime.AppRuntime;

import java.io.IOException;

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
