package symbolTable;
import ast.LangType;
import java.util.HashMap;
public class SymbolTable {
	//Classe interna per memorizzare le proprietà della variabile
	public static class Attributes{
		private LangType tipo;
		
		public Attributes(LangType tipo) {
			this.tipo=tipo;
		}
		public LangType getTipo() {
			return tipo;
		}
		
		//tabella statica (ne avremo solo una per il compilatore)
	}
	//tabella statica (ne avremo solo una per il compilatore)
	private static HashMap<String,Attributes> table = new HashMap();
	public static void init() {
		table.clear();
	}
	public static boolean enter(String id, Attributes entry) {
		if(table.containsKey(id)) {
			return false;
		}
		table.put(id, entry);
		return true;
	}
	//Cerca una variabile nella tabella
	public static Attributes lookup(String id) {
		return table.get(id);
	}
	//ritorna il numero di variabili attualmente memorizzate nella symbol table
	public static String toStr() {
		StringBuilder sb = new StringBuilder();
		sb.append("--- SYMBOL TABLE ---\n");
		
		for(String id: table.keySet()) {
			Attributes attr = table.get(id);
			sb.append("ID: ").append(id).append(" | Tipo: ").append(attr.getTipo()).append("\n");
		}
		sb.append("--------------------");
	    return sb.toString();
		
	}
	public static int size() {
		return table.size();
	}
}