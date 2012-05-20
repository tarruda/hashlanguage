package hash.vm.asm;

import hash.vm.Arg;
import hash.vm.ArgumentCount;
import hash.vm.Block;
import hash.vm.ClassGenerator;
import hash.vm.Constant;
import hash.vm.ConstructorInvocation;
import hash.vm.If;
import hash.vm.InstanceMethodInvocation;
import hash.vm.Return;
import hash.vm.StaticMethodInvocation;
import hash.vm.Throw;
import hash.vm.VirtualMachineCodeFactory;

import org.objectweb.asm.Opcodes;

public class AsmCodeFactory extends VirtualMachineCodeFactory implements
		Opcodes {

	@Override
	public ClassGenerator classGenerator(String fullname, Class superclass) {	
		AsmClassGenerator rv = new AsmClassGenerator();
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
	public Return returnStmt() {
		return new AsmReturn();
	}

	@Override
	public Throw throwStmt() {
		return new AsmThrow();
	}

}
