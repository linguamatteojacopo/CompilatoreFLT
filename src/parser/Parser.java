package parser;

import scanner.Scanner;
import scanner.LexicalException;
import token.TokenType;
import token.Token;
import ast.*;

import java.util.ArrayList;

//Il parser verifica se la sequenza di token soddisfa la grammatica del linguaggio
public class Parser {
	private scanner.Scanner sc;

	public Parser(Scanner sc) {
		this.sc = sc;
	}
	
	 public NodeProgram parse() throws SyntacticException {return this.parsePrg(); }
	 

	private Token peekWithChaining() throws SyntacticException {
		try {
			return sc.peekToken();
		} catch (LexicalException e) {
			// Chaining formale: passo l'eccezione originale come causa [5, 6]
			throw new SyntacticException("Errore lessicale durante l'ispezione del token", e);
		}
	}
	//restituisce NodeProgram che è la radice dell' albero sintattico
	private NodeProgram parsePrg() throws SyntacticException {
		Token tk = peekWithChaining();
		switch (tk.getType()) {
		case TYFLOAT, TYINT, ID, PRINT, EOF -> {
			ArrayList<NodeDecSt>decSts = parseDSs();
			match(TokenType.EOF);
			return new NodeProgram(decSts);
		}
		default -> {
			throw new SyntacticException("Token" + tk.getType() + "non valido come inizio alla riga: " + tk.getRiga());
		}
		}
	}

	private ArrayList<NodeDecSt> parseDSs() throws SyntacticException {
		Token tk = peekWithChaining();
		ArrayList<NodeDecSt> decDtS = new ArrayList<>();
		
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
	//il metodo parseDcl fa la costruzione e ritorna NodeDecl
	private NodeDecl parseDcl() throws SyntacticException {
		Token tk = peekWithChaining();
		switch (tk.getType()) {
		case TYFLOAT, TYINT -> {
			LangType type=parseTy();
			Token idToken=match(TokenType.ID);
			NodeExpr expr=parseDclP();
			NodeId idnodo= new NodeId(idToken.getValore());
			NodeDecl node= new NodeDecl(type,idnodo,expr);
			return node;
		}
		default -> {
			throw new SyntacticException("Token" + tk.getType() + "non valido come inizio alla riga: " + tk.getRiga());
		}
		}
	}

	private LangType parseTy() throws SyntacticException {
		Token tk = peekWithChaining();
		switch (tk.getType()) {
		case TYFLOAT -> {
			match(TokenType.TYFLOAT);
			return LangType.FLOAT;
		}
		case TYINT -> {
			match(TokenType.TYINT);
			return LangType.INT;
		}
		default ->
			throw new SyntacticException("Token" + tk.getType() + "non valido come inizio alla riga: " + tk.getRiga());
		}
	}

	private NodeExpr parseDclP() throws SyntacticException {
		Token tk = peekWithChaining();
		switch (tk.getType()) {
		case SEMI -> {
			match(TokenType.SEMI);
			return null;
		}
		case ASSIGN -> {
			match(TokenType.ASSIGN);
			NodeExpr exp = parseExp();
			match(TokenType.SEMI);
			return exp;
		}
		default -> {
			throw new SyntacticException(
					"Token " + tk.getType() + " non valido come inizio alla riga: " + tk.getRiga());
		}
		}

	}

	private NodeStm parseStm() throws SyntacticException {
		Token tk = peekWithChaining();
		switch (tk.getType()) {
		case ID -> {
			match(TokenType.ID);
			parseOp();
			parseExp();
			match(TokenType.SEMI);
			return null;
		}
		case PRINT -> {
			match(TokenType.PRINT);
			Token idtoken = match(TokenType.ID);//il parser legge l'id
			match(TokenType.SEMI);
			
			NodeId idnodo = new NodeId(idtoken.getValore());//mette nella scatola "piccola" il valore
			return new NodePrint(idnodo);
			
		}
		default -> {
			throw new SyntacticException(
					"Token " + tk.getType() + " non valido come inizio alla riga: " + tk.getRiga());
		}
		}
	}
	private NodeExpr parseExp() throws SyntacticException{
		Token tk= peekWithChaining();
		switch(tk.getType()) {
		case ID,FLOAT,INT->{
			parseTr();
			parseExpP();
			return null;
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
	private NodeExpr parseTr() throws SyntacticException{
		Token tk= peekWithChaining();
		switch(tk.getType()) {
		case ID, FLOAT, INT ->{
			parseVal();
			parseTrP();
			return null;
		}
		default->{
			throw new SyntacticException("Token " + tk.getType() + " non valido come inizio alla riga: " + tk.getRiga());
		}
		}
	}
	private NodeExpr parseTrP() throws SyntacticException{
		Token tk= peekWithChaining();
		switch(tk.getType()) {
		case TIMES->{
			match(TokenType.TIMES);
			parseVal();
			parseTrP();
			return null;
		}
		case DIVIDE->{
			match(TokenType.DIVIDE);
			parseVal();
			parseTrP();
			return null;
		}
		case MINUS,PLUS,SEMI ->{
			return null;
		}
		default->{
			throw new SyntacticException("Token " + tk.getType() + " non valido come inizio alla riga: " + tk.getRiga());
		}
		}
		
	}
	private NodeExpr parseVal() throws SyntacticException{
		Token tk=peekWithChaining();
		switch(tk.getType()) {
		case INT -> {
			match(TokenType.INT);
			return null;
		}
		case FLOAT -> { 
			match(TokenType.FLOAT);
			return null;
		}
		case ID -> {
			match(TokenType.ID);
			return null;
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
