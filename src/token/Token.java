package token;
/**
 * Rappresenta un token prodotto dall'analisi lessicale del linguaggio ac.
 * 
 * Ogni token è caratterizzato da:
 * - un tipo (TokenType)
 * - il numero di riga
 * - un valore opzionale (per ID, INT, FLOAT)
 */

public class Token {
	private int riga;
	private TokenType type;
	private String Valore;

	/**
     * Costruisce un token con valore associato.
     *
     * @param tipo tipo del token
     * @param riga numero di riga
     * @param Valore valore lessicale (identificatore o numero)
     */
	public Token(TokenType tipo, int riga, String Valore) {
		this.type = tipo;
		this.riga = riga;
		this.Valore = Valore;
	}
	/**
     * Costruisce un token senza valore associato.
     *
     * @param tipo tipo del token
     * @param riga numero di riga in cui è stato riconosciuto
     */
	public Token(TokenType tipo, int riga) {
		super();
		this.type = tipo;
		this.riga = riga;
	}

	/**
     * Restituisce il valore lessicale (se presente).
     *
     * @return valore del token oppure null
     */
	public String getValore() {
		return Valore;
	}

	/**
     * Restituisce il numero di riga.
     *
     * @return numero di riga
     */
	public int getRiga() {
		return riga;
	}

	/**
     * Restituisce il tipo del token.
     *
     * @return TokenType associato
     */
	public TokenType getType() {
		return type;
	}

	/**
     * Restituisce la rappresentazione testuale del token.
     *
     * @return stringa nel formato <TIPO,r:RIGA[,val:VALORE]>
     */
	@Override
	public String toString() {
		return "Token [riga=" + riga + ", type=" + type + ", Valore=" + Valore + ", toString()=" + super.toString()
				+ "]";
	}

}
