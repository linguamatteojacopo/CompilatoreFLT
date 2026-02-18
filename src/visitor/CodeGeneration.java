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
	private String codiceDc = "";
	private String log = "";

	public String getCodice() {
		return codiceDc;
	}

	public String getLog() {
		return log;
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

	// Metodo helper per recuperare il registro e validarlo
	private Character getAndCheckRegistro(String id) {
		Attributes attr = SymbolTable.lookup(id);
		// Assumiamo che se il registro è '\0' o null, i registri sono finiti
		if (attr == null || attr.getRegistro() == '\0') {
			if (log.isEmpty()) {
				log = "Errore: Registri esauriti o non assegnati per la variabile '" + id + "'";
			}
			return null;
		}
		return attr.getRegistro();
	}

	@Override
	public void visit(NodeProgram node) {
		for (NodeDecSt ds : node.getDecSts()) {
			if (log.isEmpty()) {
				ds.accept(this);
			} else {
				break;
			}
		}
	}

	@Override
	public void visit(NodeDecl node) {
		if (!log.isEmpty()) return;

		if (node.getInit() != null) {
			node.getInit().accept(this);
			
			if (log.isEmpty()) {
				Character reg = getAndCheckRegistro(node.getId().getName());
				if (reg != null) {
					codiceDc += " s" + reg;
				}
			}
		}
	}

	@Override
	public void visit(NodeAssign node) {
		if (!log.isEmpty()) return;

		node.getExpr().accept(this);
		
		if (log.isEmpty()) {
			Character reg = getAndCheckRegistro(node.getId().getName());
			if (reg != null) {
				codiceDc += " s" + reg;
			}
		}
	}

	@Override
	public void visit(NodeBinOp node) {
		if (!log.isEmpty()) return;

		node.getLeft().accept(this);
		if (!log.isEmpty()) return;

		node.getRight().accept(this);
		if (!log.isEmpty()) return;

		if (node.getOp() == LangOper.DIV_FLOAT) {
			codiceDc += " 5 k / 0 k";
		} else {
			codiceDc += " " + mapOp(node.getOp());
		}
	}

	@Override
	public void visit(NodePrint node) {
		if (!log.isEmpty()) return;

		Character reg = getAndCheckRegistro(node.getId().getName());
		if (reg != null) {
			codiceDc += " l" + reg + " p P";
		}
	}

	@Override
	public void visit(NodeDeref node) {
		if (!log.isEmpty()) return;

		Character reg = getAndCheckRegistro(node.getId().getName());
		if (reg != null) {
			codiceDc += " l" + reg;
		}
	}

	@Override
	public void visit(NodeCost node) {
		if (!log.isEmpty()) return;
		codiceDc += " " + node.getValue();
	}

	@Override
	public void visit(NodeId node) {
		// Tipicamente non fa nulla nella generazione dc
	}
}