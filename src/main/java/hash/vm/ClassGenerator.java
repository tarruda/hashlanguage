package hash.vm;

import java.util.ArrayList;
import java.util.List;

public abstract class ClassGenerator {

	private String fullname;
	private Class superclass;
	private List<MethodGenerator> methods = new ArrayList<MethodGenerator>();
	private List<ConstructorGenerator> constructors = new ArrayList<ConstructorGenerator>();

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

	public abstract MethodGenerator addMethod(String methodName, Class returnType,
			Class... parameterTypes);

	public abstract ConstructorGenerator addConstructor(Class... parameterTypes);

	public abstract byte[] generate();
}
