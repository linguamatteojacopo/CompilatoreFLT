package ast;

import visitor.IVisitor;

public class NodeId extends NodeAST{
	

	public String name;

	public NodeId(String name) {
		super();
		this.name = name;
	}
	@Override
	public void accept(IVisitor visitor) {
		visitor.visit(this);
	}
	@Override
	public String toString() {
		return "NodeId [name=" + name + "]";
	}

	public String getName() {
		return name;
	}
	
}
