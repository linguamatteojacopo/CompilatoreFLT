package ast;

public class NodeDeref extends NodeExpr{
	NodeId Id;

	public NodeDeref(NodeId id) {
		super();
		Id = id;
	}

	public NodeId getId() {
		return Id;
	}

	@Override
	public String toString() {
		return "NodeDeref [Id=" + Id + "]";
	}
	
}
