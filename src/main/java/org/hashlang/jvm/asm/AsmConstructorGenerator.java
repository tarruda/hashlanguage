package org.hashlang.jvm.asm;


import org.hashlang.jvm.ConstructorGenerator;
import org.hashlang.jvm.Statement;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class AsmConstructorGenerator extends ConstructorGenerator implements
		Opcodes {

	public void generate(ClassWriter cw, Class superclass) {
		String descriptor = Util.getMethodDescriptor(Void.TYPE,
				getParameterTypes());
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", descriptor,
				null, null);
		mv.visitCode();
		for (Statement statement : getStatements())
			((AsmStatement) statement).generate(mv);
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}
}
