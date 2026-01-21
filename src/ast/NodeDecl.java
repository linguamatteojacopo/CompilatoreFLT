package ast;

public class NodeDecl extends NodeDecSt {
	public LangType type;
	public NodeId id;
	public NodeExpr init;
	public NodeDecl(LangType type, NodeId id, NodeExpr init) {
		super();
		this.type = type;
		this.id = id;
		this.init = init;
	}
	public LangType getType() {
		return type;
	}
	public NodeId getId() {
		return id;
	}
	public NodeExpr getInit() {
		return init;
	}
	@Override
	public String toString() {
		return "NodeDecl [type=" + type + ", id=" + id + ", init=" + init + "]";
	}
	
}
