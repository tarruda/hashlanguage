package hash.jvm.asm;

import hash.jvm.MethodGenerator;
import hash.jvm.Statement;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class AsmMethodGenerator extends MethodGenerator implements Opcodes {


	public void generate(ClassWriter cw) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_VARARGS, getName(),
				Util.getMethodDescriptor(getReturnType(), getParameterTypes()),
				null, null);
		mv.visitCode();
		for (Statement statement : getStatements())
			((AsmStatement) statement).generate(mv);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}
}
