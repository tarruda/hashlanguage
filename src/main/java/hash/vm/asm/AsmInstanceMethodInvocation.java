package hash.vm.asm;

import hash.vm.Expression;
import hash.vm.InstanceMethodInvocation;

import java.lang.reflect.Method;

import org.objectweb.asm.MethodVisitor;

public class AsmInstanceMethodInvocation extends InstanceMethodInvocation
		implements AsmExpression {

	public void generate(MethodVisitor mv) {
		Method m = getMethod();
		Expression target = getTarget();
		((AsmExpression) target).generate(mv);
		Util.cast(mv, target.getDataType(), m.getDeclaringClass());
		Class[] paramTypes = m.getParameterTypes();
		for (int i = 0; i < paramTypes.length; i++) {
			Expression arg = getArg(i);
			((AsmExpression) arg).generate(mv);
			Class paramType = paramTypes[i];
			Util.cast(mv, arg.getDataType(), paramType);
			if (paramType.isPrimitive())
				Util.unbox(mv, paramType);
		}
		Util.invokeVirtual(mv, m);
	}
}