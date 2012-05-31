package org.hashlang.jvm.asm;


import org.hashlang.jvm.Expression;
import org.hashlang.jvm.Throw;
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