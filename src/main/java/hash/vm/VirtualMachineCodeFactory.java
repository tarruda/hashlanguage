package hash.vm;

import hash.vm.asm.AsmCodeFactory;

import java.lang.reflect.Constructor;

public abstract class VirtualMachineCodeFactory {

	public static final VirtualMachineCodeFactory Instance = new AsmCodeFactory();

	public Block block(Statement... statements) {
		Block rv = block();
		for (int i = 0; i < statements.length; i++)
			rv.addStatement(statements[i]);
		return rv;
	}

	public ConstructorInvocation constructorInvocation(Constructor constructor,
			Expression... args) {
		ConstructorInvocation rv = constructorInvocation();
		rv.setConstructor(constructor);
		for (int i = 0; i < args.length; i++)
			rv.addArgument(args[i]);
		return rv;
	}
	
	public InitializerInvocation initializerInvocation(Constructor constructor,
			Expression... args) {
		InitializerInvocation rv = initializerInvocation();
		rv.setConstructor(constructor);
		for (int i = 0; i < args.length; i++)
			rv.addArgument(args[i]);
		return rv;
	}

	public InstanceMethodInvocation instanceMethodInvocation(Expression target,
			java.lang.reflect.Method method, Expression... args) {
		InstanceMethodInvocation rv = instanceMethodInvocation();
		rv.setTarget(target);
		rv.setMethod(method);
		for (int i = 0; i < args.length; i++)
			rv.addArgument(args[i]);
		return rv;
	}

	public StaticMethodInvocation staticMethodInvocation(
			java.lang.reflect.Method method, Expression... args) {
		StaticMethodInvocation rv = staticMethodInvocation();
		rv.setMethod(method);
		for (int i = 0; i < args.length; i++)
			rv.addArgument(args[i]);
		return rv;
	}

	public AreEqual areEqual(Object left, Object right) {
		AreEqual rv = areEqual();
		if (!(left instanceof Expression))
			left = constant(left);
		rv.setLeft((Expression) left);
		if (!(right instanceof Expression))
			right = constant(right);
		rv.setRight((Expression) right);
		return rv;
	}

	public Constant constant(Object value) {
		Constant rv = constant();
		rv.setValue(value);
		return rv;
	}

	public Arg arg(int index) {
		Arg rv = arg();
		rv.setIndex(index);
		return rv;
	}

	public Local local(int index) {
		Local rv = local();
		rv.setIndex(index);
		rv.setType(Object.class);
		return rv;
	}
	
	public Local local(int index, Class type) {
		Local rv = local();
		rv.setIndex(index);
		rv.setType(type);
		return rv;
	}

	public InstanceOf instanceOf(Expression object, Class<?> klass) {
		InstanceOf rv = instanceOf();
		rv.setObject(object);
		rv.setBoxedClass(klass);
		return rv;
	}

	public Return returnStmt(Expression object) {
		Return rv = returnStmt();
		rv.setObject(object);
		return rv;
	}

	public Throw throwStmt(Expression object) {
		Throw rv = throwStmt();
		rv.setObject(object);
		return rv;
	}

	public InstanceOf instanceOf() {
		return new InstanceOf();
	}

	public AreEqual areEqual() {
		return new AreEqual();
	}

	public abstract ClassGenerator classGenerator(String fullname,
			Class<?> superclass);

	public abstract If ifStmt();

	public abstract Block block();

	public abstract ConstructorInvocation constructorInvocation();
	
	public abstract InitializerInvocation initializerInvocation();

	public abstract InstanceMethodInvocation instanceMethodInvocation();

	public abstract StaticMethodInvocation staticMethodInvocation();

	public abstract ArgumentCount argumentCount();

	public abstract Constant constant();

	public abstract Arg arg();

	public abstract Local local();

	public abstract Return returnStmt();

	public abstract Throw throwStmt();
}
