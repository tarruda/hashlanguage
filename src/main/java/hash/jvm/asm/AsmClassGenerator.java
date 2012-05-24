package hash.jvm.asm;

import hash.jvm.ClassGenerator;
import hash.jvm.ConstructorGenerator;
import hash.jvm.MethodGenerator;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public class AsmClassGenerator extends ClassGenerator implements Opcodes {

	@Override
	public MethodGenerator addMethod(String methodName, Class returnType,
			Class... parameterTypes) {
		MethodGenerator m = new AsmMethodGenerator();
		m.setName(methodName);
		m.setReturnType(returnType);
		m.setParameterTypes(parameterTypes);
		addMethod(m);
		return m;
	}

	@Override
	public ConstructorGenerator addConstructor(Class... parameterTypes) {
		ConstructorGenerator c = new AsmConstructorGenerator();
		c.setParameterTypes(parameterTypes);
		addConstructor(c);
		return c;
	}

	@Override
	public byte[] generate() {
		String superclassName = "java/lang/Object";
		if (getSuperclass() != null)
			superclassName = Util.internalName(getSuperclass());
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS
				+ ClassWriter.COMPUTE_FRAMES);
		cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER,
				Util.internalName(getFullname()), null, superclassName, null);
		for (MethodGenerator method : getMethods())
			((AsmMethodGenerator) method).generate(cw);
		for (ConstructorGenerator constructor : getConstructors())
			((AsmConstructorGenerator) constructor).generate(cw,
					getSuperclass());
		cw.visitEnd();
		byte[] data = cw.toByteArray();
//		try {
//			new FileOutputStream("/tmp/generated.class").write(data);
//		} catch (Exception ex) {
//			throw Err.ex(ex);
//		}
		return data;
	}

}
