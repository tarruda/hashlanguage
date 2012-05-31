package org.hashlang.jvm.asm;


import java.lang.reflect.Constructor;

import org.hashlang.jvm.InitializerInvocation;
import org.objectweb.asm.MethodVisitor;

public class AsmInitializerInvocation extends InitializerInvocation implements
		AsmStatement {

	public void generate(MethodVisitor mv) {
		Constructor c = getConstructor();
		((AsmExpression) getArg(0)).generate(mv);
		Class[] paramTypes = c.getParameterTypes();
		for (int i = 0; i < paramTypes.length; i++)
			((AsmExpression) getArg(i + 1)).generate(mv);
		Util.invokeInit(mv, c);
	}
}
