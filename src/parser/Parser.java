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
			decDtS.add(parseDcl());
			decDtS.addAll(parseDSs());
		}
		case ID, PRINT -> {
			NodeStm stm=parseStm();
			if(stm != null)
				decDtS.add(stm);
			decDtS.addAll(parseDSs());
		}
		case EOF -> {
			return decDtS;
		}
		default -> {
			throw new SyntacticException("Token" + tk.getType() + "non valido come inizio alla riga: " + tk.getRiga());
		}
		}
		return decDtS;
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
			NodeExpr left=parseTr();
			return parseExpP(left);
		}
		default ->{
			throw new SyntacticException(
					"Token " + tk.getType() + " non valido come inizio alla riga: " + tk.getRiga());
		}
		}
	}
	private NodeExpr parseExpP(NodeExpr left) throws SyntacticException{
		Token tk= peekWithChaining();
		switch(tk.getType()) {
		case PLUS ->{
			match(TokenType.PLUS);
			NodeExpr right=parseTr();
			NodeBinOp nodeop=new NodeBinOp(LangOper.PLUS,left,right);
			return parseExpP(nodeop);
		}
		case MINUS ->{
			match(TokenType.MINUS);
			NodeExpr right = parseTr();
			NodeBinOp nodeop=new NodeBinOp(LangOper.MINUS,left,right);
			return parseExpP(nodeop);
		}
		case SEMI ,EOF->{
			return left;
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
			NodeExpr left=parseVal();
			return parseTrP(left);
		}
		default->{
			throw new SyntacticException("Token " + tk.getType() + " non valido come inizio alla riga: " + tk.getRiga());
		}
		}
	}
	private NodeExpr parseTrP(NodeExpr left) throws SyntacticException{
		Token tk= peekWithChaining();
		switch(tk.getType()) {
		case TIMES->{
			match(TokenType.TIMES);
			NodeExpr right=parseVal();
			NodeBinOp nodeop= new NodeBinOp(LangOper.TIMES,left,right);
			return parseTrP(nodeop);
		}
		case DIVIDE->{
			match(TokenType.DIVIDE);
			NodeExpr right=parseVal();
			NodeBinOp nodeop= new NodeBinOp(LangOper.DIVIDE,left,right);
			return parseTrP(nodeop);
			
		}
		case MINUS,PLUS,SEMI ->{
			return left;
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
			Token t = match(TokenType.INT);
			return new NodeCost(t.getValore(),LangType.INT);
		}
		case FLOAT -> { 
			Token t = match(TokenType.FLOAT);
			return new NodeCost(t.getValore(),LangType.FLOAT);
		}
		case ID -> {
			match(TokenType.ID);
			NodeId node = new NodeId(tk.getValore());
			return new NodeDeref(node);
		}
		default->{
			throw new SyntacticException("Token " + tk.getType() + " non valido come inizio alla riga: " + tk.getRiga());
		}
		}
	}
	private LangOper parseOp() throws SyntacticException{
		Token tk=peekWithChaining();
		switch(tk.getType()) {
		case ASSIGN ->{
			match(TokenType.ASSIGN);
			return LangOper.ASSIGN;
		}
		case OP_ASSIGN -> {
			match(TokenType.OP_ASSIGN);
			if(tk.getValore().equals("+=")) return LangOper.PLUS;
			if(tk.getValore().equals("-=")) return LangOper.MINUS;
			if(tk.getValore().equals("*=")) return LangOper.TIMES;
			if(tk.getValore().equals("/=")) return LangOper.DIVIDE;
			throw new SyntacticException("Operatore composto non riconosciuto: "+ tk.getValore());


		}
		default->{
			throw new SyntacticException("Atteso operatore di assegnamento alla riga: "+tk.getRiga());
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
