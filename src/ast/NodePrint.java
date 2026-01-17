package ast;

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
	public String toString() {
		return "NodePrint [Id=" + Id + "]";
	}
}
