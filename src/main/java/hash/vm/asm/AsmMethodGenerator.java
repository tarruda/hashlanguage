package hash.vm.asm;

import hash.vm.MethodGenerator;
import hash.vm.Statement;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.sun.xml.internal.ws.org.objectweb.asm.Type;

public class AsmMethodGenerator extends MethodGenerator implements Opcodes {

	private String getMethodDescriptor(Class returnType,
			Class... parameterTypes) {
		Type[] argumentTypes = new Type[parameterTypes.length];
		for (int i = 0; i < argumentTypes.length; i++)
			argumentTypes[i] = Type.getType(parameterTypes[i]);
		return Type
				.getMethodDescriptor(Type.getType(returnType), argumentTypes);
	}

	public void generate(ClassWriter cw) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_VARARGS, getName(),
				getMethodDescriptor(getReturnType(), getParameterTypes()),
				null, null);
		mv.visitCode();
		for (Statement statement : getStatements())
			((AsmStatement) statement).generate(mv);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}
}
