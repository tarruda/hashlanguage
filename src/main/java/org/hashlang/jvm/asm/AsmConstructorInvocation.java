package org.hashlang.jvm.asm;


import java.lang.reflect.Constructor;

import org.hashlang.jvm.ConstructorInvocation;
import org.hashlang.jvm.Expression;
import org.objectweb.asm.MethodVisitor;

public class AsmConstructorInvocation extends ConstructorInvocation implements
		AsmExpression {

	public void generate(MethodVisitor mv) {
		Constructor c = getConstructor();
		mv.visitTypeInsn(NEW, Util.internalName(c.getDeclaringClass()));
		mv.visitInsn(DUP);
		Class[] paramTypes = c.getParameterTypes();
		for (int i = 0; i < paramTypes.length; i++) {
			Expression arg = getArg(i);
			((AsmExpression) arg).generate(mv);
			Class paramType = paramTypes[i];
			Util.cast(mv, arg.getDataType(), paramType);
			if (paramType.isPrimitive())
				Util.unbox(mv, paramType);
		}
		Util.invokeInit(mv, c);		
	}

}