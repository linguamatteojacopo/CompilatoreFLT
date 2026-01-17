package parser;

public class SyntacticException extends Exception {
	public SyntacticException(String message) {
		super(message);
	}

	// Costruttore per il chaining delle eccezioni (es. LexicalException)
	public SyntacticException(String message, Throwable cause) {
		super(message, cause);
	}
}
