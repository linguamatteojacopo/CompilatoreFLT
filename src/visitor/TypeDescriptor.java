package visitor;

public class TypeDescriptor {
	private final TipoTD tipo;
	private String msg;
	//costruttore per INT,FLOAT, OK
	public TypeDescriptor(TipoTD tipo) {
		this.tipo=tipo;
	}
	//costruttore per ERROR
	public TypeDescriptor(TipoTD tipo, String msg) {
		this.tipo = tipo;
		this.msg = msg;
	}
	public TipoTD getTipo(){
		return tipo;
	}
	public String getmsg() {
		return msg;
	}
	public boolean compatible(TypeDescriptor tD) {
		if(this.tipo == tD.getTipo()) {
			return true;
		}
		//Regola in ac: INT può essere applicato a FLOAT
		if(this.getTipo() == TipoTD.FLOAT && tD.getTipo() == TipoTD.INT) {
			return true;
		}
		return false;
	}
	public boolean isError() {
		if(this.tipo==TipoTD.ERROR) {
			return true;
		}
		return false;
	}
	@Override
	public String toString() {
		return "TypeDescriptor [tipo=" + tipo + ", msg=" + msg + "]";
	}
}
