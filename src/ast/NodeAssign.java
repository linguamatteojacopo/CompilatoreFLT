package ast;

public class NodeAssign extends NodeStm{
	private NodeId id;
	private NodeExpr expr;
	public NodeAssign(NodeId id, NodeExpr expr) {
		super();
		this.id = id;
		this.expr = expr;
	}
	public NodeId getId() {
		return id;
	}
	public NodeExpr getExpr() {
		return expr;
	}
	@Override
	public String toString() {
		return "NodeAssign [id=" + id + ", expr=" + expr + "]";
	}
	
}
