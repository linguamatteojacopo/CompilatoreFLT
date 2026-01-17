package ast;

public class NodeId {
	

	public String name;

	public NodeId(String name) {
		super();
		this.name = name;
	}

	@Override
	public String toString() {
		return "NodeId [name=" + name + "]";
	}

	public String getName() {
		return name;
	}
	
}
