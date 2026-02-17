package visitor;

import ast.NodeProgram;
import ast.NodeDecl;
import ast.NodeDeref;
import ast.NodePrint;
import ast.NodeAssign;
import ast.LangOper;
import ast.NodeBinOp;
import ast.NodeDecSt;
import ast.NodeCost;
import ast.NodeId;
import symbolTable.SymbolTable;
import symbolTable.SymbolTable.Attributes;


public class CodeGeneration implements IVisitor {
	private String codiceDc="";
	
	public String getCodice() {
		return codiceDc;
	}
	private String mapOp(LangOper op) {
	    switch (op) {
	        case PLUS:   return "+";
	        case MINUS:  return "-";
	        case TIMES:  return "*";
	        case DIVIDE: return "/";
	        default:     return ""; 
	    }
	}
	@Override
	public void visit(NodeProgram node) {
		for(NodeDecSt ds: node.getDecSts()) {
			ds.accept(this);
		}
	}
	@Override
	public void visit(NodeDecl node) {
		if(node.getInit()!=null) {
			node.getInit().accept(this);
			codiceDc += "s" + node.getId().getName();
		}
	}
	@Override
	public void visit(NodeAssign node) {
		node.getExpr().accept(this);
		codiceDc=" s" + node.getId().getName();
	}
	@Override
	public void visit(NodeBinOp node) {
		node.getLeft().accept(this);
		node.getRight().accept(this);
		
		if(node.getOp() == LangOper.DIV_FLOAT) {
			codiceDc += "5 k / 0 k";
		}
		else
			codiceDc += " " + mapOp(node.getOp());
	}
	@Override
	//p stampa e P manda a capo
	public void visit(NodePrint node) {
		codiceDc += " l"+node.getId().getName() + " p P";
	}
	@Override
	//carica sullo stack il valore della variabile
	public void visit(NodeDeref node) {
		codiceDc += "l"+node.getId().getName();
	}
	@Override
	public void visit(NodeCost node) {
		codiceDc += " "+node.getValue();
	}
	@Override
	public void visit(NodeId node) {
		
	}

}
