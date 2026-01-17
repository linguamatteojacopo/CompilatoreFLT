package parser;

import scanner.Scanner;
import scanner.LexicalException;
import token.TokenType;
import token.Token;

//Il parser verifica se la sequenza di token soddisfa la grammatica del linguaggio
public class Parser {
	private scanner.Scanner sc;

	public Parser(Scanner sc) {
		this.sc = sc;
	}
	/*
	 * public void parse() throws SyntacticException { return this.parsePrg(); }
	 */

	private Token peekWithChaining() throws SyntacticException {
		try {
			return sc.peekToken();
		} catch (LexicalException e) {
			// Chaining formale: passo l'eccezione originale come causa [5, 6]
			throw new SyntacticException("Errore lessicale durante l'ispezione del token", e);
		}
	}

	private void parsePrg() throws SyntacticException {
		Token tk = peekWithChaining();
		switch (tk.getType()) {
		case TYFLOAT, TYINT, ID, PRINT, EOF -> {
			parseDSs();
			match(TokenType.EOF);
		}
		default -> {
			throw new SyntacticException("Token" + tk.getType() + "non valido come inizio alla riga: " + tk.getRiga());
		}
		}
	}

	private void parseDSs() throws SyntacticException {
		Token tk = peekWithChaining();
		switch (tk.getType()) {
		case TYFLOAT, TYINT -> {
			parseDcl();
			parseDSs();
		}
		case ID, PRINT -> {
			parseStm();
			parseDSs();
		}
		case EOF -> {
			return;
		}
		default -> {
			throw new SyntacticException("Token" + tk.getType() + "non valido come inizio alla riga: " + tk.getRiga());
		}
		}

	}

	private void parseDcl() throws SyntacticException {
		Token tk = peekWithChaining();
		switch (tk.getType()) {
		case TYFLOAT, TYINT -> {
			parseTy();
			match(TokenType.ID);
			parseDclP();
		}
		default -> {
			throw new SyntacticException("Token" + tk.getType() + "non valido come inizio alla riga: " + tk.getRiga());
		}
		}
	}

	private void parseTy() throws SyntacticException {
		Token tk = peekWithChaining();
		switch (tk.getType()) {
		case TYFLOAT -> match(TokenType.TYFLOAT);
		case TYINT -> match(TokenType.TYINT);
		default ->
			throw new SyntacticException("Token" + tk.getType() + "non valido come inizio alla riga: " + tk.getRiga());
		}
	}

	private void parseDclP() throws SyntacticException {
		Token tk = peekWithChaining();
		switch (tk.getType()) {
		case SEMI -> match(TokenType.SEMI);
		case ASSIGN -> {
			match(TokenType.ASSIGN);
			parseExp();
			match(TokenType.SEMI);
		}
		default -> {
			throw new SyntacticException(
					"Token " + tk.getType() + " non valido come inizio alla riga: " + tk.getRiga());
		}
		}

	}

	private void parseStm() throws SyntacticException {
		Token tk = peekWithChaining();
		switch (tk.getType()) {
		case ID -> {
			match(TokenType.ID);
			parseOp();
			parseExp();
			match(TokenType.SEMI);
		}
		case PRINT -> {
			match(TokenType.PRINT);
			match(TokenType.ID);
			match(TokenType.SEMI);
		}
		default -> {
			throw new SyntacticException(
					"Token " + tk.getType() + " non valido come inizio alla riga: " + tk.getRiga());
		}
		}
	}
	private void parseExp() throws SyntacticException{
		Token tk= peekWithChaining();
		switch(tk.getType()) {
		case ID,FLOAT,INT->{
			parseTr();
			parseExpP();
		}
		default ->{
			throw new SyntacticException(
					"Token " + tk.getType() + " non valido come inizio alla riga: " + tk.getRiga());
		}
		}
	}
	private void parseExpP() throws SyntacticException{
		Token tk= peekWithChaining();
		switch(tk.getType()) {
		case PLUS ->{
			match(TokenType.PLUS);
			parseTr();
			parseExpP();
		}
		case MINUS ->{
			match(TokenType.MINUS);
			parseTr();
			parseExpP();
		}
		case SEMI ->{
			return;
		}
		default->{
			throw new SyntacticException("Token " + tk.getType() + " non valido come inizio alla riga: " + tk.getRiga());
		}
		}
	}
	private void parseTr() throws SyntacticException{
		Token tk= peekWithChaining();
		switch(tk.getType()) {
		case ID, FLOAT, INT ->{
			parseVal();
			parseTrP();
		}
		default->{
			throw new SyntacticException("Token " + tk.getType() + " non valido come inizio alla riga: " + tk.getRiga());
		}
		}
	}
	private void parseTrP() throws SyntacticException{
		Token tk= peekWithChaining();
		switch(tk.getType()) {
		case TIMES->{
			match(TokenType.TIMES);
			parseVal();
			parseTrP();
		}
		case DIVIDE->{
			match(TokenType.DIVIDE);
			parseVal();
			parseTrP();
		}
		case MINUS,PLUS,SEMI ->{
			return;
		}
		default->{
			throw new SyntacticException("Token " + tk.getType() + " non valido come inizio alla riga: " + tk.getRiga());
		}
		}
		
	}
	private void parseVal() throws SyntacticException{
		Token tk=peekWithChaining();
		switch(tk.getType()) {
		case INT -> {
			match(TokenType.INT);
		}
		case FLOAT -> { 
			match(TokenType.FLOAT);
		}
		case ID -> {
			match(TokenType.ID);
		}
		default->{
			throw new SyntacticException("Token " + tk.getType() + " non valido come inizio alla riga: " + tk.getRiga());
		}
		}	
	}
	private void parseOp() throws SyntacticException{
		Token tk=peekWithChaining();
		switch(tk.getType()) {
		case ASSIGN ->{
			match(TokenType.ASSIGN);
		}
		case OP_ASSIGN -> {
			match(TokenType.OP_ASSIGN);
		}
		default->{
			throw new SyntacticException("Token " + tk.getType() + " non valido come inizio alla riga: " + tk.getRiga());
		}
		}
	}
	// metodo parse

	/*
	 * Questo metodo ha il compito di: • Controllare se il tipo del prossimo token
	 * (ottenuto tramite peekToken()) corrisponde a quello atteso. • Consumare il
	 * token chiamando nextToken() se il tipo è corretto. • Sollevare una
	 * SyntacticException se il tipo non corrisponde, indicando la riga e il motivo
	 * dell'errore.
	 */
	private Token match(TokenType Type) throws SyntacticException {
		Token t = peekWithChaining();
		if (Type.equals(t.getType())) {
			try {
				return sc.nextToken(); // Consuma e ritorna il token [7, 8]
			} catch (LexicalException e) {
				throw new SyntacticException("Errore lessicale durante il consumo del token", e);
			}
		} else {
			throw new SyntacticException(
					"Errore Sintattico: atteso " + Type + " ma trovato " + t.getType() + " alla riga " + t.getRiga());

		}
	}

}
