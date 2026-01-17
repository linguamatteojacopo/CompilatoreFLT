package scanner;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import token.*;

public class Scanner {
	final char EOF = (char) -1;
	private int riga;
	private PushbackReader buffer;
	/*
	 * Liste: Per rappresentare insiemi di caratteri è preferibile usare strutture
	 * come Set<Character>
	 */
	private Set<Character> skipChars;// skpChars: insieme caratteri di skip (include EOF) e inizializzazione
	private Set<Character> letters; // letters: insieme lettere
	private Set<Character> digits; // digits: cifre

	private Map<Character, TokenType> operTkType;// operTkType: mapping fra caratteri '+', '-', '*', '/' e il TokenType
													// corrispondente
	private Map<Character, TokenType> delimTkType;// delimTkType: mapping fra caratteri '=', ';' e il e il TokenType
													// corrispondente
	private Map<String, TokenType> keyWordsTkType; // keyWordsTkType: mapping fra le stringhe "print", "float", "int" e
													// il TokenType corrispondente

	// campo per nextToken
	private Token nextTk;

	public Scanner(String fileName) throws FileNotFoundException {

		this.buffer = new PushbackReader(new FileReader(fileName));
		riga = 1;
		// inizializzare campi che non hanno inizializzazione
		initSetsAndMaps();
	}

	public Scanner(int riga, PushbackReader buffer, Set<Character> skipChars, Set<Character> letters,
			Set<Character> digits, Map<Character, TokenType> operTkType, Map<Character, TokenType> delimTkType,
			Map<String, TokenType> keyWordsTkType) {
		super();
		this.riga = riga;
		this.buffer = buffer;
		this.skipChars = skipChars;
		this.letters = letters;
		this.digits = digits;
		this.operTkType = operTkType;
		this.delimTkType = delimTkType;
		this.keyWordsTkType = keyWordsTkType;
	}

	// ------------------------------------------------------------
	// Inizializzazione insiemi e mappe
	// ------------------------------------------------------------
	private void initSetsAndMaps() {
		// caratteri di skip
		skipChars = new HashSet<>();
		skipChars.add(' ');
		skipChars.add('\t');
		skipChars.add('\r');
		skipChars.add('\n');
		// NB: non metto EOF qui, lo gestisco esplicitamente

		// lettere
		letters = new HashSet<>();
		for (char c = 'a'; c <= 'z'; c++)
			letters.add(c);
		for (char c = 'A'; c <= 'Z'; c++)
			letters.add(c);

		// cifre
		digits = new HashSet<>();
		for (char c = '0'; c <= '9'; c++)
			digits.add(c);

		// operatori aritmetici
		operTkType = new HashMap<>();
		operTkType.put('+', TokenType.PLUS);
		operTkType.put('-', TokenType.MINUS);
		operTkType.put('*', TokenType.TIMES);
		operTkType.put('/', TokenType.DIVIDE);

		// delimitatori (=, ;)
		delimTkType = new HashMap<>();
		delimTkType.put('=', TokenType.ASSIGN);
		delimTkType.put(';', TokenType.SEMI);

		// parole chiave: int, float, print
		keyWordsTkType = new HashMap<>();
		keyWordsTkType.put("int", TokenType.TYINT);
		keyWordsTkType.put("float", TokenType.TYFLOAT);
		keyWordsTkType.put("print", TokenType.PRINT);
	}

	// nextToken ritorna il prossimo token nel file di input e legge
	// i caratteri del token ritornato (avanzando fino al carattere
	// successivo all'ultimo carattere del token)
	public Token nextToken() throws LexicalException {
		char nextChar;
		if (nextTk != null) {
			Token t = nextTk;
			nextTk = null;
			return t;
		}
		// nextChar contiene il prossimo carattere dell'input (non consumato).
		try {
			nextChar = peekChar(); // Catturate l'eccezione IOException e
			// ritornate una LexicalException che la contiene
		} catch (IOException e) {
			throw new LexicalException("Errore alla riga: " + riga + e);
		}

		// Avanza nel buffer leggendo i carattere in skipChars
		// incrementando riga se leggi '\n'.
		while (skipChars.contains(nextChar)) {
			try {
				char c = readChar();
				if (c == '\n')
					riga++;
				nextChar = peekChar();
			} catch (IOException e) {
				throw new LexicalException("Errore di I/O durante lo skip dei caratteri alla riga: " + riga + e);
			}
		}
		// Se raggiungi la fine del file ritorna il Token EOF
		if (nextChar == EOF) {
			return new Token(TokenType.EOF, riga, "EOF");
		}
		// Se nextChar e' in letters
		// return scanId()
		// che deve generare o un Token ID o parola chiave
		if (letters.contains(nextChar)) {
			return ScanId();
		}
		// Se nextChar e' o in operators oppure delimitatore
		// ritorna il Token associato con l'operatore o il delimitatore
		// Attenzione agli operatori di assegnamento!
		if (operTkType.containsKey(nextChar) || delimTkType.containsKey(nextChar)) {
			return scanOperator();
		}
		// Se nextChar e' ; o =
		// ritorna il Token associato
		if (digits.contains(nextChar)) {
			return scanNumber();
		}

		throw new LexicalException("Carattere illegale '" + nextChar + "' alla riga " + riga);
	}

	public Token peekToken() throws LexicalException {
		if (nextTk == null) {
			nextTk = nextToken();
		}
		return nextTk;
	}

	// Regola: ID-->lettera(lettera | cifra)*
	// dallo stato 0 se leggo una lettera entro nel 3, continuo a leggere lettere o
	// cifre finchè non avanzo
	private Token ScanId() throws LexicalException {
		StringBuilder sb = new StringBuilder();
		try {
			char c = readChar(); // 1- Entrata nello stato 3: stato 0--> leggo una lettera --> transizione a
									// stato 3
			sb.append(c);

			char next = peekChar(); // 2- Serve a decidere se l' automa può avanzare senza consumare caratteri
									// inutilmente
			while (letters.contains(next) || digits.contains(next)) { // 3- accumulo il lexeme andando avanti il più
																		// possibile
				c = readChar();
				sb.append(c);
				next = peekChar();
			} // 4- se non è ne' lettera nè cifra esco dal loop
		} catch (IOException e) {
			throw new LexicalException("Errore di I/O leggendo un identificatore alla riga " + riga + e);
		}
		String lexeme = sb.toString(); // lexeme= sequenza di caratteri letti da input
		// parola chiave?
		TokenType kwType = keyWordsTkType.get(lexeme);
		if (kwType != null) {
			return new Token(kwType, riga, lexeme);
		}

		// altrimenti ID
		return new Token(TokenType.ID, riga, lexeme);
	}// non serve controllo di errore perchè scanId viene chiamato solo se il primo
		// carattere è lettera

	private Token scanOperator() throws LexicalException// devo scannerizzare l' operatore, prima di farlo capisco se
														// serve con peekChar()
	{
		char first;
		// leggo e consumo il primo carattere
		try {
			first = readChar();
		} catch (IOException e) {
			throw new LexicalException("Errore di I/O alla riga " + riga + e);
		}
		// caso operatori aritmetici composti
		if (operTkType.containsKey(first)) {
			try {
				char next = peekChar();

				if (next == '=') {
					readChar();
					String lexeme = "" + first + "=";
					return new Token(TokenType.OP_ASSIGN, riga, lexeme);
				}
			} catch (IOException e) {
				throw new LexicalException("Errore alla riga " + riga + e);
			}
			return new Token(operTkType.get(first), riga, Character.toString(first));
		}
		// Caso delimitatori: = ;
		if (delimTkType.containsKey(first)) {
			return new Token(delimTkType.get(first), riga, Character.toString(first));
		}

		// caso impossibile (per sicurezza)
		throw new LexicalException("Carattere '" + first + "' non valido alla riga " + riga);

	}

	/*
	 * Se nextChar e' in numbers return scanNumber() // che legge sia un intero che
	 * un float e ritorna il Token INUM o FNUM // i caratteri che leggete devono
	 * essere accumulati in una stringa // che verra' assegnata al campo valore del
	 * Token
	 * 
	 * // Altrimenti il carattere NON E' UN CARATTERE LEGALE sollevate una //
	 * eccezione lessicale dicendo la riga e il carattere che la hanno // provocata.
	 */
	private Token scanNumber() throws LexicalException {
		StringBuilder sb = new StringBuilder();
		char c;
		int contDec = 0;
		try {
			c = readChar();
			sb.append(c);
			char next = peekChar();
			while (digits.contains(next)) {
				c = readChar();
				sb.append(c);
				next = peekChar();
			}
			if (next != '.') {
				String lexeme = sb.toString();
				return new Token(TokenType.INT, riga, lexeme);
			}

			// Float: consuma il punto
			c = readChar();
			sb.append(c);

			next = peekChar();
			if (!digits.contains(next)) {
				throw new LexicalException("Numero non valido alla riga " + riga + ": manca la parte decimale");
			}
			while (digits.contains(next)) {
				c = readChar();
				sb.append(c);
				contDec++;
				if (contDec > 5) {
					throw new LexicalException("Numero reale con piu' di 5 cifre decimali alla riga " + riga);
				}
				next = peekChar();
			}
			return new Token(TokenType.FLOAT, riga, sb.toString());
		} catch (IOException e) {
			throw new LexicalException("Errore di I/O leggendo un numero alla riga " + riga + e);
		}

	}

	public Scanner(int riga, PushbackReader buffer) {
		super();
		this.riga = riga;
		this.buffer = buffer;
	}

	private char readChar() throws IOException {
		return ((char) this.buffer.read());
	}

	private char peekChar() throws IOException {
		char c = (char) buffer.read();
		buffer.unread(c);
		return c;
	}
}
