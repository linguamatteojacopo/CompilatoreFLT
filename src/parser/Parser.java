package parser;

import scanner.Scanner;
import scanner.LexicalException;
import token.TokenType;
import token.Token;
import ast.*;

import java.util.ArrayList;
/**
 * La classe Parser implementa un analizzatore sintattico a discesa ricorsiva 
 * per il linguaggio 'ac' 
 * Il parser riceve i token dallo Scanner e verifica che la sequenza rispetti 
 * la grammatica libera da contesto definita per il linguaggio.
 * Durante il processo di riconoscimento, il parser costruisce l'Abstract Syntax Tree (AST), che rappresenta la struttura semantica del programma.
 */

public class Parser {
	private scanner.Scanner sc;

	public Parser(Scanner sc) {
		this.sc = sc;
	}
	/**
     * Avvia l'analisi sintattica del programma. 
     * Rappresenta il punto di ingresso per il simbolo non terminale iniziale 'Prg'.
     * * @return Il nodo radice dell'AST di tipo {@link NodeProgram}.
     * @throws SyntacticException Se viene riscontrato un errore di sintassi o un errore lessicale durante il parsing.
     */
	 public NodeProgram parse() throws SyntacticException {return this.parsePrg(); }
	 

	private Token peekWithChaining() throws SyntacticException {
		try {
			return sc.peekToken();
		} catch (LexicalException e) {
			// Chaining formale: passo l'eccezione originale come causa [5, 6]
			throw new SyntacticException("Errore lessicale durante l'ispezione del token", e);
		}
	}
	/**
     * Analizza il non terminale Prg -> DSs $.
     * * @return Un oggetto {@link NodeProgram} contenente la lista di dichiarazioni e istruzioni.
     * @throws LexicalException In caso di errore nel recupero del token.
     * @throws SyntacticException In caso di violazione della grammatica o mancanza di EOF.
     */
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
	/**
     * Analizza una sequenza di dichiarazioni e istruzioni (DSs).
     * Gestisce la ricorsione della grammatica per accumulare i nodi in una lista.
     * * @return Una {@link ArrayList} di {@link NodeDecSt}.
     * @throws SyntacticException In caso di errore sintattico.
     */
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
	/**
	 * Parsifica una dichiarazione
	 * Segue la produzione: Dcl -> Ty id DclP
	 * @return Un oggetto {@link NodeDecl} che rappresenta la dichiarazione nell'AST.
	 */
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
	/**
	 * Parsifica un tipo
	 * 
	 * La grammatica utilizzata è: Ty -> TYFLOAT | TYINT
	 * @return Il tipo corrispondente {@link LangType#FLOAT} o {@link LangType#INT}.
     * @throws SyntacticException Se il token corrente non è un tipo valido.
	 */
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
	/**
     * Analizza la parte opzionale di una dichiarazione (DclP).
     * <p>Grammatica: DclP -> ; | = Exp ;</p>
     * * @return Un oggetto {@link NodeExpr} se è presente un'inizializzazione, 
     * null se la dichiarazione termina con ';'.
     * @throws SyntacticException Se la sintassi dopo l'identificatore è errata.
     */
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
	/**
     * Analizza un'istruzione (Stm), che può essere un assegnamento o una stampa.
     * <p>Grammatica: Stm -> id Op Exp; | print id;</p>
     * <p>In caso di operatori composti (es. +=), trasforma l'istruzione in un 
     * nodo di assegnamento standard (es. a = a + exp) nell'AST.</p>
     * * @return Un nodo che estende {@link NodeStm} ({@link NodeAssign} o {@link NodePrint}).
     * @throws SyntacticException Se l'istruzione è malformata.
     */
	private NodeStm parseStm() throws SyntacticException {
		Token tk = peekWithChaining();
		switch (tk.getType()) {
		case ID -> {
			Token idtk = match(TokenType.ID);
			NodeId idNodo= new NodeId(idtk.getValore());
			LangOper op =parseOp();
			NodeExpr exp=parseExp();
			match(TokenType.SEMI);
			
			if(op == LangOper.ASSIGN) {
				return new NodeAssign(idNodo,exp);
			}
			else {
				NodeDeref deref = new NodeDeref(idNodo);
				NodeBinOp binop= new NodeBinOp(op,deref,exp);
				return new NodeAssign(idNodo,binop);
			}
			
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
	/**
     * Punto di ingresso per l'analisi di un'espressione (Exp).
     * <p>Grammatica: Exp -> Tr ExpP</p>
     * * @return Un nodo {@link NodeExpr} che rappresenta l'espressione completa.
     * @throws SyntacticException Se l'espressione non inizia con un token valido.
     */
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
	/**
     * Gestisce la ricorsione destra per l'addizione e la sottrazione (ExpP).
     * <p>Grammatica: ExpP -> + Tr ExpP | - Tr ExpP | SEMI</p>
     * * @param left Il sotto-albero sinistro già analizzato.
     * @return Un nodo {@link NodeExpr} che integra l'operatore e l'operando destro, 
     * o il nodo 'left' se non ci sono più operatori additivi.
     * @throws SyntacticException Se viene trovato un operatore senza un termine valido.
     */
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
	/**
     * Analizza un termine (Tr), gestendo la precedenza di moltiplicazione e divisione.
     * <p>Grammatica: Tr -> Val TrP</p>
     * * @return Un nodo {@link NodeExpr} che rappresenta il termine analizzato.
     * @throws SyntacticException Se il termine non inizia con un valore valido.
     */
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
	/**
     * Gestisce la ricorsione destra per la moltiplicazione e la divisione (TrP).
     * <p>Grammatica: TrP -> * Val TrP | / Val TrP | epsilon</p>
     * * @param left Il sotto-albero sinistro già analizzato.
     * @return Un nodo {@link NodeExpr} integrato con operatori moltiplicativi, 
     * o il nodo 'left' se non ci sono più operatori.
     * @throws SyntacticException Se la sintassi del termine è incompleta.
     */
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
	/**
     * Analizza i valori atomici di un'espressione (Val).
     * <p>Grammatica: Val -> intVal | floatVal | id</p>
     * * @return Un nodo {@link NodeCost} per i letterali o {@link NodeDeref} per le variabili.
     * @throws SyntacticException Se il token non è un valore costante o un identificatore.
     */
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
			Token t =match(TokenType.ID);
			NodeId node = new NodeId(t.getValore());
			return new NodeDeref(node);
		}
		default->{
			throw new SyntacticException("Token " + tk.getType() + " non valido come inizio alla riga: " + tk.getRiga());
		}
		}
	}
	/**
     * Analizza l'operatore di assegnamento (Op).
     * <p>Grammatica:  Op -> ASSIGN | OP_ASSIGN</p>
     * * @return L'operatore corrispondente della gerarchia {@link LangOper}.
     * @throws SyntacticException Se l'operatore non è riconosciuto.
     */
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
	

	/**
     * Verifica che il prossimo token sia del tipo atteso e lo consuma.
     * <p>Implementa il controllo sintattico fondamentale del parser a discesa ricorsiva.</p>
     * * @param Type Il tipo di token ({@link TokenType}) atteso.
     * @return Il {@link Token} consumato.
     * @throws SyntacticException Se il token corrente non corrisponde a Type 
     * o se si verifica un errore lessicale.
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
