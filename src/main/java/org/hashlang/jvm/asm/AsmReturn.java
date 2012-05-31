package org.hashlang.jvm.asm;


import org.hashlang.jvm.Expression;
import org.hashlang.jvm.Return;
import org.objectweb.asm.MethodVisitor;

public class AsmReturn extends Return implements AsmStatement {

	public void generate(MethodVisitor mv) {
		Expression obj = getObject();
		Class dt = null;
		if (obj == null || (dt = obj.getDataType()) == Void.TYPE)
			mv.visitInsn(ACONST_NULL);
		else {
			((AsmExpression) obj).generate(mv);
			Util.box(mv, dt);
		}
		mv.visitInsn(ARETURN);
	}
}