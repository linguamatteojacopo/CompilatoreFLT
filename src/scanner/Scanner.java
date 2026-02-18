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
/**
 * Implementa l'analizzatore lessicale per il linguaggio ac.
 * 
 * Produce una sequenza di Token a partire da un file sorgente.
 * Supporta lookahead tramite peekToken().
 */
public class Scanner {
	final char EOF = (char) -1;
	private int riga;
	private PushbackReader buffer;
	private Set<Character> skipChars;
	private Set<Character> letters; 
	private Set<Character> digits; 

	private Map<Character, TokenType> operTkType;
	private Map<Character, TokenType> delimTkType;
	private Map<String, TokenType> keyWordsTkType;
	private Token nextTk;

	/**
     * Costruisce uno scanner associato a un file sorgente.
     *
     * @param fileName percorso del file da analizzare
     * @throws IOException se il file non esiste o non è leggibile
     */
	public Scanner(String fileName) throws FileNotFoundException {

		this.buffer = new PushbackReader(new FileReader(fileName));
		riga = 1;
		initSetsAndMaps();
	}
	/**
	 * Costruttore per test o inizializzazione manuale con parametri specifici.
	 * * @param riga numero di riga iniziale
	 * @param buffer il reader con supporto pushback per gestire l'input
	 * @param skipChars insieme dei caratteri da ignorare (spazi, tab, ecc.)
	 * @param letters insieme dei caratteri alfabetici validi
	 * @param digits insieme delle cifre numeriche (0-9)
	 * @param operTkType mapping tra caratteri operatore e TokenType
	 * @param delimTkType mapping tra caratteri delimitatori e TokenType
	 * @param keyWordsTkType mapping tra stringhe parole chiave e TokenType
	 */
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
	/**
	 * Inizializza le strutture dati necessarie al riconoscimento dei pattern.
	 * Definisce i caratteri di skip, i set di caratteri validi per ID e costanti,
	 * e popola le mappe delle parole riservate (int, float, print) e degli operatori.
	 */
	private void initSetsAndMaps() {
		skipChars = new HashSet<>();
		skipChars.add(' ');
		skipChars.add('\t');
		skipChars.add('\r');
		skipChars.add('\n');
		
		letters = new HashSet<>();
		for (char c = 'a'; c <= 'z'; c++)
			letters.add(c);
		for (char c = 'A'; c <= 'Z'; c++)
			letters.add(c);

		
		digits = new HashSet<>();
		for (char c = '0'; c <= '9'; c++)
			digits.add(c);

		
		operTkType = new HashMap<>();
		operTkType.put('+', TokenType.PLUS);
		operTkType.put('-', TokenType.MINUS);
		operTkType.put('*', TokenType.TIMES);
		operTkType.put('/', TokenType.DIVIDE);

		
		delimTkType = new HashMap<>();
		delimTkType.put('=', TokenType.ASSIGN);
		delimTkType.put(';', TokenType.SEMI);

		
		keyWordsTkType = new HashMap<>();
		keyWordsTkType.put("int", TokenType.TYINT);
		keyWordsTkType.put("float", TokenType.TYFLOAT);
		keyWordsTkType.put("print", TokenType.PRINT);
	}

	/**
     * Restituisce il prossimo token consumando input.
     *
     * @return prossimo Token riconosciuto
     * @throws LexicalException in caso di errore lessicale
     */
	public Token nextToken() throws LexicalException {
		char nextChar;
		if (nextTk != null) {
			Token t = nextTk;
			nextTk = null;
			return t;
		}
		try {
			nextChar = peekChar(); 
		} catch (IOException e) {
			throw new LexicalException("Errore alla riga: " + riga + e);
		}
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
		if (nextChar == EOF) {
			return new Token(TokenType.EOF, riga, "EOF");
		}
		if (letters.contains(nextChar)) {
			return ScanId();
		}
		
		if (operTkType.containsKey(nextChar) || delimTkType.containsKey(nextChar)) {
			return scanOperator();
		}
		
		if (digits.contains(nextChar)) {
			return scanNumber();
		}

		throw new LexicalException("Carattere illegale '" + nextChar + "' alla riga " + riga);
	}
	/**
     * Ispeziona il prossimo token senza consumare l'input.
     * 
     * <p>Permette al Parser di guardare avanti (lookahead) per decidere quale regola 
     * grammaticale applicare. Chiamate successive a peekToken() o una chiamata a 
     * nextToken() restituiranno lo stesso token.</p>
     * 
     * @return Il prossimo {@link Token} senza avanzare nel flusso.
     * @throws LexicalException Se la lettura del prossimo token causa un errore lessicale.
     */

	public Token peekToken() throws LexicalException {
		if (nextTk == null) {
			nextTk = nextToken();
		}
		return nextTk;
	}

	/**
     * Metodo privato per il riconoscimento degli identificatori e delle parole chiave.
     * Dopo aver accumulato il lessema, controlla se corrisponde a una parola riservata 
     * (es. 'print', 'int', 'float').
     * 
     * @return Un Token di tipo ID o una parola chiave specifica.
     * @throws LexicalException In caso di errori di I/O durante la lettura.
     */
	
	private Token ScanId() throws LexicalException {
		StringBuilder sb = new StringBuilder();
		try {
			char c = readChar(); 
			
			sb.append(c);

			char next = peekChar(); 
			
			while (letters.contains(next) || digits.contains(next)) { 
				c = readChar();
				sb.append(c);
				next = peekChar();
			}
		} catch (IOException e) {
			throw new LexicalException("Errore di I/O leggendo un identificatore alla riga " + riga + e);
		}
		String lexeme = sb.toString(); 
		
		TokenType kwType = keyWordsTkType.get(lexeme);
		if (kwType != null) {
			return new Token(kwType, riga, lexeme);
		}

		return new Token(TokenType.ID, riga, lexeme);
	}

	/**
	 * Metodo privato per il riconoscimento degli operatori e dei delimitatori.
	 * Gestisce gli operatori aritmetici, l'assegnamento semplice (=) e gli operatori 
	 * di assegnamento composto (es. +=, -=, ecc.).
	 * * @return Un Token corrispondente all'operatore o al delimitatore riconosciuto.
	 * @throws LexicalException Se il carattere non è valido o in caso di errore di lettura.
	 */
	private Token scanOperator() throws LexicalException
	{
		char first;
		
		try {
			first = readChar();
		} catch (IOException e) {
			throw new LexicalException("Errore di I/O alla riga " + riga + e);
		}
		
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
		
		if (delimTkType.containsKey(first)) {
			return new Token(delimTkType.get(first), riga, Character.toString(first));
		}

		
		throw new LexicalException("Carattere '" + first + "' non valido alla riga " + riga);

	}

	 /**
     * Metodo privato per il riconoscimento delle costanti numeriche (Interi e Float).
     * 
     * <p>Valida la struttura dei numeri floating point, assicurandosi che non abbiano 
     * più di 5 cifre decimali dopo il punto, come richiesto dalle specifiche ac.</p>
     * 
     * @return Un Token di tipo INT o FLOAT.
     * @throws LexicalException Se il numero è malformato o supera i limiti di precisione.
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
	/**
	 * Costruttore secondario per inizializzare lo scanner con riga e buffer correnti.
	 * * @param riga numero di riga da cui partire
	 * @param buffer buffer di lettura
	 */
	public Scanner(int riga, PushbackReader buffer) {
		super();
		this.riga = riga;
		this.buffer = buffer;
	}
	/**
	 * Legge il carattere successivo dal buffer di input.
	 * * @return il carattere letto
	 * @throws IOException in caso di problemi di lettura fisica del file
	 */
	private char readChar() throws IOException {
		return ((char) this.buffer.read());
	}
	/**
	 * Legge il carattere successivo dal buffer senza consumarlo.
	 * Utilizza le funzionalità del PushbackReader per restituire il carattere al buffer.
	 * * @return il carattere ispezionato
	 * @throws IOException in caso di problemi di lettura o di pushback del carattere
	 */
	private char peekChar() throws IOException {
		char c = (char) buffer.read();
		buffer.unread(c);
		return c;
	}
}
