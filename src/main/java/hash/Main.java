package hash;

import hash.program.repl.REPL;
import hash.runtime.AppRuntime;

import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException {
		AppRuntime runtime = new AppRuntime();		
		REPL.start(runtime);		
	}
}
