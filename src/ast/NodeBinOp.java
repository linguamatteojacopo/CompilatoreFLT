package ast;
import visitor.IVisitor;
public class NodeBinOp extends NodeExpr{
	private LangOper op;
	private NodeExpr left;
	private NodeExpr right;
	public NodeBinOp(LangOper op, NodeExpr left, NodeExpr right) {
		super();
		this.op = op;
		this.left = left;
		this.right = right;
	}
	public LangOper getOp() {
		return op;
	}
	public void setOp(LangOper op) {
		this.op=op;
	}
	public NodeExpr getLeft() {
		return left;
	}
	public NodeExpr getRight() {
		return right;
	}
	@Override
	public String toString() {
		return "NodeBinOp [op=" + op + ", left=" + left + ", right=" + right + "]";
	}
	@Override
	public void accept(IVisitor visitor) {
		visitor.visit(this);
	}
	

	
}
