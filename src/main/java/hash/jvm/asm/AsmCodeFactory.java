package hash.jvm.asm;

import hash.jvm.Arg;
import hash.jvm.ArgumentCount;
import hash.jvm.Block;
import hash.jvm.ClassGenerator;
import hash.jvm.Constant;
import hash.jvm.ConstructorInvocation;
import hash.jvm.If;
import hash.jvm.InitializerInvocation;
import hash.jvm.InstanceMethodInvocation;
import hash.jvm.Local;
import hash.jvm.Return;
import hash.jvm.StaticMethodInvocation;
import hash.jvm.Throw;
import hash.jvm.VirtualMachineCodeFactory;

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
