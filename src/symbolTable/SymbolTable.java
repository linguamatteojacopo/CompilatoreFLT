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
	public static String toStr() {
	// ......
	}
	public static int size() {
	// ......
	}
}