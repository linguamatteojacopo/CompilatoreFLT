package visitor;
/**
 * La classe {@code TypeDescriptor} rappresenta il descrittore di tipo utilizzato durante 
 * la fase di analisi semantica (Type Checking) del compilatore.
 * <p>
 * Viene utilizzata per verificare la correttezza dei tipi nelle espressioni, 
 * nelle dichiarazioni e negli assegnamenti, gestendo anche la propagazione dei messaggi di errore.
 * </p>
 * * 
 */
public class TypeDescriptor {
	/** Il tipo atomico rappresentato da questo descrittore (INT, FLOAT, OK, ERROR). */
	private final TipoTD tipo;
	/** Messaggio di errore associato, valorizzato solo se il tipo è {@code TipoTD.ERROR}. */
	private String msg;
	/**
     * Costruisce un descrittore di tipo standard (INT, FLOAT o OK).
     * * @param tipo Il {@link TipoTD} da assegnare al descrittore.
     */
	public TypeDescriptor(TipoTD tipo) {
		this.tipo=tipo;
	}
	/**
     * Costruisce un descrittore di tipo per rappresentare un errore semantico.
     * * @param tipo Deve essere {@code TipoTD.ERROR}.
     *   @param msg Il messaggio descrittivo dell'errore riscontrato.
     */
	public TypeDescriptor(TipoTD tipo, String msg) {
		this.tipo = tipo;
		this.msg = msg;
	}
	/**
     * Restituisce il tipo atomico del descrittore.
     * * @return l'enum {@link TipoTD} associato.
     */
	public TipoTD getTipo(){
		return tipo;
	}
	/**
     * Restituisce il messaggio di errore se presente.
     * * @return la stringa contenente il messaggio di errore, o {@code null} se non è un errore.
     */
	public String getmsg() {
		return msg;
	}
	/**
     * Verifica la compatibilità tra questo tipo e un altro descrittore, 
     * seguendo le regole del linguaggio ac.
     * <p>
     * Regola di coercizione: un valore di tipo {@code INT} può essere assegnato o 
     * utilizzato dove è richiesto un {@code FLOAT} (conversione automatica).
     * </p>
     * * @param tD Il descrittore di tipo da confrontare.
     * @return {@code true} se i tipi sono uguali o se questo è FLOAT e l'altro è INT; 
     * {@code false} altrimenti.
     */
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
	/**
     * Verifica se questo descrittore rappresenta un errore semantico.
     * * @return {@code true} se il tipo è {@code TipoTD.ERROR}, {@code false} altrimenti.
     */
	public boolean isError() {
		if(this.tipo==TipoTD.ERROR) {
			return true;
		}
		return false;
	}
	/**
     * Restituisce una rappresentazione testuale del descrittore di tipo.
     * * @return una stringa contenente il tipo e l'eventuale messaggio d'errore.
     */
	@Override
	public String toString() {
		return "TypeDescriptor [tipo=" + tipo + ", msg=" + msg + "]";
	}
}
