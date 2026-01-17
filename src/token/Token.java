package token;

public class Token {
	// int riga
	// token type
	// valore
	private int riga;
	private TokenType type;
	private String Valore;// opzionale, definisco un costruttore apposta

	// costruttore
	public Token(TokenType tipo, int riga, String Valore) {
		this.type = tipo;
		this.riga = riga;
		this.Valore = Valore;
	}

	public Token(TokenType tipo, int riga) {
		super();
		this.type = tipo;
		this.riga = riga;
	}

	public String getValore() {
		return Valore;
	}

	public int getRiga() {
		return riga;
	}

	public TokenType getType() {
		return type;
	}

	@Override
	public String toString() {
		return "Token [riga=" + riga + ", type=" + type + ", Valore=" + Valore + ", toString()=" + super.toString()
				+ "]";
	}

}
