package symbolTable;
import ast.LangType;
import java.util.HashMap;
/**
 * La classe {@code SymbolTable} rappresenta la tabella dei simboli utilizzata dal compilatore
 * per gestire le dichiarazioni delle variabili durante le fasi di type-checking e 
 * generazione del codice.
 * <p>
 * Implementa una tabella statica  che associa il nome di un identificatore ai suoi attributi (tipo e registro).
 * </p>
 * *
 */
public class SymbolTable {
	/**
     * Classe interna che rappresenta gli attributi associati a un identificatore.
     * Memorizza il tipo semantico e il registro assegnato per la generazione del codice.
     */
	public static class Attributes{
		private LangType tipo;
		private char registro;
		/**
         * Costruisce un nuovo oggetto Attributes.
         * * @param tipo Il tipo di dato della variabile (INT o FLOAT).
         * @param registro Il carattere che identifica il registro dc associato.
         */
		public Attributes(LangType tipo,char registro) {
			this.tipo=tipo;
			this.registro = registro;		
		}
		/**
         * Restituisce il tipo associato all'identificatore.
         * @return il {@link LangType} della variabile.
         */
		public LangType getTipo() {
			return tipo;
		}
		/**
         * Restituisce il registro associato all'identificatore.
         * @return il carattere del registro.
         */
		public char getRegistro() {
			return registro;
		}
		
	}
	
	private static HashMap<String,Attributes> table = new HashMap();
	/**
     * Inizializza o svuota la Symbol Table. 
     * Da invocare all'inizio di ogni nuova compilazione.
     */
	public static void init() {
		table.clear();
	}
	/**
     * Inserisce una nuova associazione nella tabella dei simboli.
     * * @param id Il nome dell'identificatore (la chiave).
     * @param entry L'oggetto {@link Attributes} contenente tipo e registro.
     * @return {@code true} se l'inserimento ha successo; {@code false} se l'identificatore 
     * è già presente (errore di doppia dichiarazione).
     */
	public static boolean enter(String id, Attributes entry) {
		if(table.containsKey(id)) {
			return false;
		}
		table.put(id, entry);
		return true;
	}
	/**
     * Cerca un identificatore nella tabella e ne restituisce gli attributi.
     * * @param id Il nome della variabile da cercare.
     * @return L'oggetto {@link Attributes} associato, o {@code null} se l'identificatore 
     * non è stato dichiarato.
     */
	public static Attributes lookup(String id) {
		return table.get(id);
	}
	/**
     * Restituisce una rappresentazione testuale dello stato attuale della Symbol Table.
     * Utilizzato principalmente per scopi di debug.
     * * @return Una stringa formattata contenente tutti gli ID e i relativi tipi.
     */
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
	/**
     * Restituisce il numero di variabili attualmente memorizzate nella tabella.
     * * @return il numero di entry presenti.
     */
	public static int size() {
		return table.size();
	}
}