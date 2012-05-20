package hash.vm.asm;

import hash.vm.Expression;
import hash.vm.StaticMethodInvocation;

import java.lang.reflect.Method;

import org.objectweb.asm.MethodVisitor;

public class AsmStaticMethodInvocation extends StaticMethodInvocation implements
		AsmExpression {

	public void generate(MethodVisitor mv) {
		Method m = getMethod();
		Class[] paramTypes = m.getParameterTypes();
		for (int i = 0; i < paramTypes.length; i++) {
			Expression arg = getArg(i);
			((AsmExpression) arg).generate(mv);
			Class paramType = paramTypes[i];
			Util.cast(mv, arg.getDataType(), paramType);
			if (paramType.isPrimitive())
				Util.unbox(mv, paramType);
		}
		Util.invokeStatic(mv, m);
	}

}