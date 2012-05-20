package hash.vm.asm;

import hash.vm.Expression;
import hash.vm.Throw;

import org.objectweb.asm.MethodVisitor;

public class AsmThrow extends Throw implements AsmStatement {

	public void generate(MethodVisitor mv) {
		Expression obj = getObject();
		Class dt = obj.getDataType();
		((AsmExpression) obj).generate(mv);
		Util.cast(mv, dt, Throwable.class);
		mv.visitInsn(ATHROW);
	}

}