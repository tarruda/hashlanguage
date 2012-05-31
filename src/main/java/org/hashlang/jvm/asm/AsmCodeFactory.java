package org.hashlang.jvm.asm;


import org.hashlang.jvm.Arg;
import org.hashlang.jvm.ArgumentCount;
import org.hashlang.jvm.Block;
import org.hashlang.jvm.ClassGenerator;
import org.hashlang.jvm.Constant;
import org.hashlang.jvm.ConstructorInvocation;
import org.hashlang.jvm.If;
import org.hashlang.jvm.InitializerInvocation;
import org.hashlang.jvm.InstanceMethodInvocation;
import org.hashlang.jvm.Local;
import org.hashlang.jvm.Return;
import org.hashlang.jvm.StaticMethodInvocation;
import org.hashlang.jvm.Throw;
import org.hashlang.jvm.VirtualMachineCodeFactory;
import org.objectweb.asm.Opcodes;

public class AsmCodeFactory extends VirtualMachineCodeFactory implements
		Opcodes {

	@Override
	public ClassGenerator classGenerator(String fullname, Class superclass) {
		AsmClassGenerator rv = new AsmClassGenerator();
		rv.setFactory(this);
		rv.setFullname(fullname);
		rv.setSuperclass(superclass);
		return rv;
	}

	@Override
	public If ifStmt() {
		return new AsmIf();
	}

	@Override
	public Block block() {
		return new AsmBlock();
	}

	@Override
	public ConstructorInvocation constructorInvocation() {
		return new AsmConstructorInvocation();
	}
	
	@Override
	public InitializerInvocation initializerInvocation() {
		return new AsmInitializerInvocation();
	}

	@Override
	public InstanceMethodInvocation instanceMethodInvocation() {
		return new AsmInstanceMethodInvocation();
	}

	@Override
	public StaticMethodInvocation staticMethodInvocation() {
		return new AsmStaticMethodInvocation();
	}

	@Override
	public ArgumentCount argumentCount() {
		return new AsmArgumentCount();
	}

	@Override
	public Constant constant() {
		return new AsmConstant();
	}

	@Override
	public Arg arg() {
		return new AsmArg();
	}

	@Override
	public Local local() {
		return new AsmLocal();
	}

	@Override
	public Return returnStmt() {
		return new AsmReturn();
	}

	@Override
	public Throw throwStmt() {
		return new AsmThrow();
	}

}
