package ast;

import visitor.IVisitor;

public class NodePrint extends NodeStm {
	public NodeId Id;
	public NodePrint(NodeId id) {
		super();
		this.Id = id;
	}
	public NodeId getId() {
		return Id;
	}
	@Override
	public void accept(IVisitor visitor) {
		visitor.visit(this);
	}
	@Override
	public String toString() {
		return "NodePrint [Id=" + Id + "]";
	}
}
