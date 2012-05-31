package org.hashlang;


import java.io.IOException;

import org.hashlang.program.repl.REPL;
import org.hashlang.runtime.AppRuntime;

public class Main {
	public static void main(String[] args) throws IOException {
		AppRuntime runtime = new AppRuntime();		
		REPL.start(runtime);		
	}
}
