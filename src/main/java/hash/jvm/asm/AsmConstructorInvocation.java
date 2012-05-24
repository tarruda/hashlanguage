package hash.jvm.asm;

import hash.jvm.ConstructorInvocation;
import hash.jvm.Expression;

import java.lang.reflect.Constructor;

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