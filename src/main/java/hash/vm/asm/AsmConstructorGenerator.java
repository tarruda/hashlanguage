package hash.vm.asm;

import hash.vm.ConstructorGenerator;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class AsmConstructorGenerator extends ConstructorGenerator implements
		Opcodes {

	public void generate(ClassWriter cw, Class superclass) {
		String descriptor = null;
		try {
			descriptor = Type.getConstructorDescriptor(superclass
					.getConstructor(getArgTypes()));

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", descriptor,
				null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		int i = 1;
		for (Class<?> argType : getArgTypes()) {
			Util.load(mv, argType, i);
			i++;
		}
		mv.visitMethodInsn(INVOKESPECIAL, Util.internalName(superclass),
				"<init>", descriptor);
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}
}
