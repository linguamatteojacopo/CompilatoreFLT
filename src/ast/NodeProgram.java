package ast;
import  java.util.ArrayList;
public class NodeProgram extends NodeAST {
	private ArrayList<NodeDecSt> decSts;
	public NodeProgram(ArrayList<NodeDecSt> decSts) {
		this.decSts=decSts;
	}
	public ArrayList<NodeDecSt> getDecSts() {
		return decSts;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("NodeProgram(");
		for(int i=0;i<decSts.size();i++) {
			sb.append(decSts.get(i).toString());
			if(i< decSts.size() -1) {
				sb.append(",");
			}
		}
		sb.append(")");
		return sb.toString();
	}
	
}
