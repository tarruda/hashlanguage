package org.hashlang.jvm.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public interface AsmStatement extends Opcodes {

	void generate(MethodVisitor mv);
}
