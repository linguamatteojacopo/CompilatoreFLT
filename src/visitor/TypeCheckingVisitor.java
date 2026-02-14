package visitor;
import ast.*;
import symbolTable.SymbolTable;
import symbolTable.SymbolTable.Attributes;
public class TypeCheckingVisitor implements IVisitor {
	private TypeDescriptor resType;//memorizzare e propagare il risultato dell' analisi

	
	public TypeCheckingVisitor(TypeDescriptor resType) {
		super();
		this.resType = resType;
	}

	public TypeDescriptor getResType() {
		return resType;
	}
	public void visit(NodeDecl node) {
		Attributes attr = new Attributes(node.getType());
		if(!SymbolTable.enter(node.getId().getName(), attr)) {
			resType = new TypeDescriptor(TipoTD.ERROR,"Errore: variabile gia' creata");
			return;
		}
		//Controllo eventuale inizializzazione
		if(node.getInit() != null) {
			node.getInit().accept(this);
			TypeDescriptor initType=resType;
			TypeDescriptor declType= new TypeDescriptor(node.getType()==LangType.INT ? TipoTD.INT : TipoTD.FLOAT);
			
			if (!declType.compatibile(initType)) {
                resType = new TypeDescriptor(TipoTD.ERROR, "Errore: Tipi incompatibili nell'inizializzazione di " + node.getId().getName());
            } else {
                resType = new TypeDescriptor(TipoTD.OK);
            }
		}else {
			resType = new TypeDescriptor(TipoTD.OK);
		}
	}
	public void visit(NodeBinOp node) {
		node.getLeft().accept(this);
		TypeDescriptor leftTD= resType;
		
		node.getRight().accept(this);
		TypeDescriptor rightTD = resType;
		if(leftTD.getTipo() == TipoTD.FLOAT || rightTD.getTipo() == TipoTD.FLOAT) {
			resType = new TypeDescriptor(TipoTD.FLOAT);
		}
		else {
			resType = new TypeDescriptor(TipoTD.INT);
		}
	}
	public void visit(NodeId node) {
		Attributes lookup = SymbolTable.lookup(node.getName());
		if(lookup!=null) {
			if(lookup.getTipo()==LangType.INT) {
				resType= new TypeDescriptor(TipoTD.INT);
			}else {
				resType = new TypeDescriptor(TipoTD.FLOAT);
			}	
		}
		else
		{
			new TypeDescriptor(TipoTD.ERROR,"Errore semantico: variabile");
		}
	}
	public void visit(NodeDeref node) {
		node.getId().accept(this);
	}
	

}
