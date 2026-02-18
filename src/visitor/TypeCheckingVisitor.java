package visitor;
import ast.*;
import symbolTable.SymbolTable;
import symbolTable.SymbolTable.Attributes;
public class TypeCheckingVisitor implements IVisitor {
	private char prossimoRegistro = 'a'; //Contatore per i registri
	private TypeDescriptor resType;//memorizzare e propagare il risultato dell' analisi

	public TypeCheckingVisitor(TypeDescriptor resType) {
		super();
		SymbolTable.init();
		this.resType = resType;
	}

	public TypeDescriptor getResType() {
		return resType;
	}

	//punto d' ingresso, coordina la visita dei componenti che formano l'AST
	public void visit(NodeProgram node) {
		for(NodeDecSt ds: node.getDecSts()) {//itera per controllare, che ogni elemento venga processato correttamente
			ds.accept(this); //invoca il metodo visit corretto a seconda del tipo di dichiarazione o istruzione presente
			if(resType.isError()) {
				System.out.println(resType.getmsg());
			}
		}
	}
	
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
	
	public void visit(NodePrint node) {
		node.getId().accept(this);
		if(!resType.isError()) {
			resType= new TypeDescriptor(TipoTD.OK);
		}
	}
	
	
	/*
	 * visit(NodeAssign node): Simile alla dichiarazione, deve controllare che la variabile 
	 *  a sinistra sia presente nella tabella e che l'espressione a destra sia compatibile con il tipo 
	 *  della variabile
	 */
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
	public void visit(NodeCost node) {
		resType = new TypeDescriptor(node.getType() == LangType.INT ? TipoTD.INT : TipoTD.FLOAT);
	}
	
	public void visit(NodeDeref node) {
		node.getId().accept(this);
	}
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
