package org.hashlang.jvm;


import java.util.ArrayList;
import java.util.List;

import org.hashlang.util.Err;

public abstract class ClassGenerator {

	private VirtualMachineCodeFactory factory;
	private String fullname;
	private Class superclass;
	private List<MethodGenerator> methods = new ArrayList<MethodGenerator>();
	private List<ConstructorGenerator> constructors = new ArrayList<ConstructorGenerator>();

	public VirtualMachineCodeFactory getFactory() {
		return factory;
	}

	public void setFactory(VirtualMachineCodeFactory factory) {
		this.factory = factory;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public Class getSuperclass() {
		return superclass;
	}

	public void setSuperclass(Class superclass) {
		this.superclass = superclass;
	}

	protected void addMethod(MethodGenerator m) {
		methods.add(m);
	}

	protected void addConstructor(ConstructorGenerator c) {
		constructors.add(c);
	}

	public Iterable<MethodGenerator> getMethods() {
		return methods;
	}

	public Iterable<ConstructorGenerator> getConstructors() {
		return constructors;
	}

	public MethodGenerator getMethod(int i) {
		return methods.get(i);
	}

	public ConstructorGenerator getConstructor(int i) {
		return constructors.get(i);
	}

	public void addSimpleConstructor(Class... parameterTypes) {
		InitializerInvocation superInvocation = factory.initializerInvocation();
		try {
			superInvocation.setConstructor(getSuperclass().getConstructor(
					parameterTypes));
		} catch (Exception e) {
			throw Err.ex(e);
		}
		superInvocation.addArgument(factory.local(0));
		for (int i = 0; i < parameterTypes.length; i++)
			superInvocation
					.addArgument(factory.local(i + 1, parameterTypes[i]));
		ConstructorGenerator c = addConstructor(parameterTypes);
		c.addStatement(superInvocation);
	}

	public abstract MethodGenerator addMethod(String methodName,
			Class returnType, Class... parameterTypes);

	public abstract ConstructorGenerator addConstructor(Class... parameterTypes);

	public abstract byte[] generate();
}
