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
/**
 * La classe {@code CodeGeneration} implementa la fase finale del compilatore: la generazione del codice.
 * <p>
 * Utilizza il pattern Visitor per attraversare l'AST e produrre codice dc.
 *  Il codice generato si basa sulla notazione 
 * polacca inversa (RPN) e utilizza i registri di {@code dc} per memorizzare le variabili.
 * </p>
 * * 
 */
public class CodeGeneration implements IVisitor {
	/** Stringa che accumula il codice dc generato durante la visita. */
	private String codiceDc = "";
	/** Stringa utilizzata per loggare errori fatali durante la generazione (es. esaurimento registri). */
	private String log = "";

	/**
	 * Restituisce il codice dc finale generato.
	 * @return La stringa contenente le istruzioni dc.
	 */
	public String getCodice() {
		return codiceDc;
	}

	/**
	 * Restituisce i log di errore riscontrati durante la generazione.
	 * @return Una stringa di errore, o una stringa vuota se la generazione ha avuto successo.
	 */
	public String getLog() {
		return log;
	}

	/**
	 * Mappa gli operatori definiti nel linguaggio ({@link LangOper}) nei corrispondenti 
	 * simboli operatore di {@code dc}.
	 * @param op L'operatore AST da mappare.
	 * @return Una stringa contenente l'operatore dc equivalente.
	 */
	private String mapOp(LangOper op) {
		switch (op) {
			case PLUS:   return "+";
			case MINUS:  return "-";
			case TIMES:  return "*";
			case DIVIDE: return "/";
			default:     return "";
		}
	}

	/**
	 * Metodo helper che recupera l'attributo registro dalla {@link SymbolTable} per un dato ID.
	 * Se il registro non è stato assegnato o la variabile non esiste, popola il log di errore.
	 * @param id Il nome dell'identificatore.
	 * @return Il carattere del registro dc associato, o {@code null} in caso di errore.
	 */
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

	/**
	 * Coordina la generazione del codice visitando sequenzialmente tutte le dichiarazioni 
	 * e le istruzioni del programma.
	 * @param node Il nodo radice del programma.
	 */
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

	/**
	 * Genera il codice per una dichiarazione. Se è presente un'inizializzazione, 
	 * valuta l'espressione e ne memorizza il valore nel registro assegnato (comando {@code s}).
	 * @param node Il nodo di dichiarazione.
	 */
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

	/**
	 * Genera il codice per un assegnamento. Valuta l'espressione e salva il 
	 * risultato nel registro associato all'identificatore.
	 * @param node Il nodo di assegnamento.
	 */
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

	/**
	 * Genera il codice per un'operazione binaria in notazione postfissa.
	 * Gestisce in modo speciale la divisione tra float impostando la precisione di {@code dc} a 5 decimali.
	 * @param node Il nodo dell'operazione binaria.
	 */
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

	/**
	 * Genera il codice per stampare il valore di una variabile.
	 * Utilizza il comando {@code l} per caricare dal registro e {@code p P} per stampare e pulire lo stack.
	 * @param node Il nodo print.
	 */
	@Override
	public void visit(NodePrint node) {
		if (!log.isEmpty()) return;

		Character reg = getAndCheckRegistro(node.getId().getName());
		if (reg != null) {
			codiceDc += " l" + reg + " p P";
		}
	}

	/**
	 * Genera il codice per caricare il valore di una variabile (dereferenziazione).
	 * Utilizza il comando {@code l} seguito dal registro.
	 * @param node Il nodo deref.
	 */
	@Override
	public void visit(NodeDeref node) {
		if (!log.isEmpty()) return;

		Character reg = getAndCheckRegistro(node.getId().getName());
		if (reg != null) {
			codiceDc += " l" + reg;
		}
	}
	/**
	 * Aggiunge una costante numerica (intero o float) al codice dc.
	 * @param node Il nodo costante.
	 */
	@Override
	public void visit(NodeCost node) {
		if (!log.isEmpty()) return;
		codiceDc += " " + node.getValue();
	}

	/**
	 * Nodo identificatore. Non produce codice direttamente poiché l'ID viene 
	 * gestito dai nodi che lo utilizzano (Assign, Deref, Print) per accedere ai registri.
	 * @param node Il nodo identificatore.
	 */
	@Override
	public void visit(NodeId node) {
	
	}
}