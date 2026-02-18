package visitor;
import ast.*;
import symbolTable.SymbolTable;
import symbolTable.SymbolTable.Attributes;
/**
 * La classe {@code TypeCheckingVisitor} implementa l'analisi semantica del compilatore 
 * utilizzando il pattern Visitor.
 * <p>
 * Questa fase si occupa di:
 * <ul>
 * <li>Verificare che ogni variabile sia dichiarata prima dell'uso.</li>
 * <li>Controllare la compatibilità dei tipi negli assegnamenti e nelle espressioni.</li>
 * <li>Effettuare la promozione di tipo (coercizione) da INT a FLOAT dove necessario.</li>
 * <li>Assegnare i registri alle variabili per la successiva fase di generazione del codice.</li>
 * </ul>
 * </p>
 * * 
 */
public class TypeCheckingVisitor implements IVisitor {
	/** Contatore utilizzato per assegnare un registro univoco (carattere) a ogni variabile dichiarata. */
	private char prossimoRegistro = 'a'; 
	/** Descrittore di tipo utilizzato per memorizzare e propagare il risultato dell'analisi durante la visita dei nodi. */
	private TypeDescriptor resType;

	/**
     * Costruisce un nuovo TypeCheckingVisitor.
     * Inizializza la {@link SymbolTable} per una nuova sessione di analisi.
     * * @param resType Il descrittore di tipo iniziale.
     */
	public TypeCheckingVisitor(TypeDescriptor resType) {
		super();
		SymbolTable.init();
		this.resType = resType;
	}

	/**
     * Restituisce l'ultimo descrittore di tipo calcolato.
     * @return il {@link TypeDescriptor} corrente.
     */
	public TypeDescriptor getResType() {
		return resType;
	}

	/**
     * Punto d'ingresso dell'analisi. Coordina la visita di tutti i nodi 
     * di dichiarazione e istruzione che compongono il programma.
     * * @param node Il nodo radice del programma.
     */
	@Override
	public void visit(NodeProgram node) {
		for(NodeDecSt ds: node.getDecSts()) {//itera per controllare, che ogni elemento venga processato correttamente
			ds.accept(this); //invoca il metodo visit corretto a seconda del tipo di dichiarazione o istruzione presente
			if(resType.isError()) {
				System.out.println(resType.getmsg());
			}
		}
	}
	/**
     * Controlla se l'identificatore è presente nella Symbol Table.
     * Se presente, collega l'entry della tabella al nodo per utilizzi futuri.
     * * @param node Il nodo che rappresenta l'identificatore.
     */
    @Override
	public void visit(NodeId node) {
		Attributes lookup = SymbolTable.lookup(node.getName());
		if(lookup!=null) {
			node.setEntry(lookup);
			if(lookup.getTipo()==LangType.INT) {
				resType= new TypeDescriptor(TipoTD.INT);
			}else {
				resType = new TypeDescriptor(TipoTD.FLOAT);
			}	
		}
		else
		{
			resType = new TypeDescriptor(TipoTD.ERROR,"Errore semantico: variabile '"+node.getName() + "' non dichiarata");
		}
	}
    /**
     * Gestisce la dichiarazione di una variabile. 
     * Verifica la doppia dichiarazione, assegna un registro e, se presente, 
     * valida il tipo dell'espressione di inizializzazione.
     * * @param node Il nodo di dichiarazione.
     */
    @Override
	public void visit(NodeDecl node) {
		Attributes attr = new Attributes(node.getType(),prossimoRegistro++);
		if(!SymbolTable.enter(node.getId().getName(), attr)) {
			resType = new TypeDescriptor(TipoTD.ERROR,"Errore: variabile gia' creata");
			return;
		}
		//Controllo eventuale inizializzazione
		if(node.getInit() != null) {
			//parte sinistra
			node.getInit().accept(this);
			TypeDescriptor initType=resType;
			if(resType.isError()) {
				resType=initType;
				return;
			}
			TypeDescriptor declType= new TypeDescriptor(node.getType()==LangType.INT ? TipoTD.INT : TipoTD.FLOAT);
			
			if (!declType.compatible(initType)) {
                resType = new TypeDescriptor(TipoTD.ERROR, "Errore: Tipi incompatibili nell'inizializzazione di " + node.getId().getName());
            } else {
                resType = new TypeDescriptor(TipoTD.OK);
            }
		}else {
			resType = new TypeDescriptor(TipoTD.OK);
		}
	}
	
    /**
     * Valida l'istruzione di stampa verificando che l'identificatore esista.
     * * @param node Il nodo print.
     */
    @Override
	public void visit(NodePrint node) {
		node.getId().accept(this);
		if(!resType.isError()) {
			resType= new TypeDescriptor(TipoTD.OK);
		}
	}
	
	
    /**
     * Gestisce l'assegnamento di un valore a una variabile.
     * Verifica che la variabile a sinistra sia dichiarata e che il tipo 
     * dell'espressione a destra sia compatibile.
     * * @param node Il nodo di assegnamento.
     */
    @Override
	public void visit(NodeAssign node) {
		node.getId().accept(this);
		TypeDescriptor idType = resType;
		if(idType.isError()) {
			resType=idType;
			return;
		}
		
		node.getExpr().accept(this);
		TypeDescriptor expType = resType;
		if(expType.isError()){
			resType=expType;
			return;
		}
		
		if(idType.compatible(expType)) 
			resType = new TypeDescriptor(TipoTD.OK);
		else
			resType = new TypeDescriptor(TipoTD.ERROR,"Errore tipi incompatibili");
		
	}
    /**
     * Determina il tipo di una costante (INT o FLOAT).
     * * @param node Il nodo costante.
     */
    @Override
	public void visit(NodeCost node) {
		resType = new TypeDescriptor(node.getType() == LangType.INT ? TipoTD.INT : TipoTD.FLOAT);
	}
    /**
     * Gestisce il recupero del valore di una variabile (dereferenziazione).
     * * @param node Il nodo deref.
     */
    @Override
	public void visit(NodeDeref node) {
		node.getId().accept(this);
	}
    /**
     * Analizza un'operazione binaria (+, -, *, /).
     * Determina il tipo risultante in base agli operandi: se almeno uno è FLOAT, 
     * il risultato è FLOAT. Gestisce inoltre la specializzazione dell'operatore 
     * di divisione per virgola mobile (DIV_FLOAT).
     * * 
     * * @param node Il nodo dell'operazione binaria.
     */
    @Override
	public void visit(NodeBinOp node) {
		node.getLeft().accept(this);
		TypeDescriptor leftTD= resType;
		if(leftTD.isError()) {
		    resType = leftTD;
		    return;
		}
		
		node.getRight().accept(this);
		TypeDescriptor rightTD = resType;
		if(rightTD.isError()) {
		    resType = rightTD;
		    return;
		}
		if(leftTD.getTipo() == TipoTD.FLOAT || rightTD.getTipo() == TipoTD.FLOAT) {
			resType = new TypeDescriptor(TipoTD.FLOAT);
			
			
			if (node.getOp() == LangOper.DIVIDE) {
                node.setOp(LangOper.DIV_FLOAT); 
            }
		}
		else {
			resType = new TypeDescriptor(TipoTD.INT);
		}
	}
	
	
	

}
