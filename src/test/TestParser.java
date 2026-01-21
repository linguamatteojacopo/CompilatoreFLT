package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import parser.Parser;
import parser.SyntacticException;
import scanner.Scanner;
import java.io.FileNotFoundException;
import ast.*;
class TestParser {

    // Percorso base per i file di test come indicato nel progetto [4, 5]
    private final String basePath = "src/test/data/";

    /**
     * Test per programmi sintatticamente corretti.
     * Il parser non deve lanciare alcuna eccezione [1, 3].
     */
    @Test
    void testParserCorretto() {
        assertDoesNotThrow(() -> {
            // Test con file forniti: testParserCorretto1.txt e testParserCorretto2.txt [6, 7]
            new Parser(new Scanner(basePath + "testParserCorretto1.txt")).parse();
            new Parser(new Scanner(basePath + "testParserCorretto2.txt")).parse();
            // Test solo dichiarazioni e print [8, 9]
            new Parser(new Scanner(basePath + "testSoloDich.txt")).parse();
            new Parser(new Scanner(basePath + "testSoloDichPrint.txt")).parse();
        }, "Il parser dovrebbe accettare questi programmi senza errori.");
    }

    /**
     * Test per programmi con errori sintattici.
     * Il parser deve sollevare una SyntacticException [1, 2].
     */
    @Test
    void testParserErrori() {
        // testParserEcc_0: Identificatore senza assegnamento o dichiarazione [10]
        assertThrows(SyntacticException.class, () -> 
            new Parser(new Scanner(basePath + "testParserEcc_0.txt")).parse());

        // testParserEcc_1: Doppio operatore consecutivo [11]
        assertThrows(SyntacticException.class, () -> 
            new Parser(new Scanner(basePath + "testParserEcc_1.txt")).parse());

        // testParserEcc_2: Identificatore che inizia con un numero [12]
        assertThrows(SyntacticException.class, () -> 
            new Parser(new Scanner(basePath + "testParserEcc_2.txt")).parse());

        // testParserEcc_3: Espressione senza assegnamento [13]
        assertThrows(SyntacticException.class, () -> 
            new Parser(new Scanner(basePath + "testParserEcc_3.txt")).parse());

        // testParserEcc_4: Print di un numero (solo print id è ammesso) [14]
        assertThrows(SyntacticException.class, () -> 
            new Parser(new Scanner(basePath + "testParserEcc_4.txt")).parse());

        // testParserEcc_5: Errore nella dichiarazione (float seguito da numero) [15]
        assertThrows(SyntacticException.class, () -> 
            new Parser(new Scanner(basePath + "testParserEcc_5.txt")).parse());

        // testParserEcc_6: Uso di parola chiave come identificatore [16]
        assertThrows(SyntacticException.class, () -> 
            new Parser(new Scanner(basePath + "testParserEcc_6.txt")).parse());

        // testParserEcc_7: Assegnamento diretto senza identificatore valido [17]
        assertThrows(SyntacticException.class, () -> 
            new Parser(new Scanner(basePath + "testParserEcc_7.txt")).parse());
    }

    /**
     * Una volta completata la costruzione dell'AST, il test dovrebbe confrontare 
     * il toString() dell'albero con il valore atteso [18, 19].
     */
    @Test
    void testProduzioneASTCorretto() throws Exception{
        Scanner scanner = new Scanner (basePath +"testAST.txt");
        Parser parser = new Parser (scanner);
        
        //Avvio del parser per la costruzione dell' albeero
        NodeProgram root = parser.parse();
        String astOutput= root.toString();
        
        //Debug albero
        System.out.println("DEBUG AST: " + astOutput);
        //Verifica Dichiarazione semplice(int temp;)
        assertTrue(astOutput.contains("NodeDecl [type=INT, id=NodeId [name=temp], init=null]"), 
                "Manca la dichiarazione corretta di temp");
        //Per l'assegnamento:
        assertTrue(astOutput.contains("NodeAssign [id=NodeId [name=temp]"), 
                "L'assegnamento non è stato costruito correttamente");
        assertTrue(astOutput.contains ("expr=NodeBinOp [op=PLUS, left=NodeDeref [Id=NodeId [name=temp]], right=NodeCost [value=7, type=INT]]],NodeAssign [id=NodeId [name=temp], expr=NodeBinOp [op=MINUS, left=NodeBinOp [op=PLUS, left=NodeCost [value=3, type=INT], right=NodeBinOp [op=TIMES, left=NodeCost [value=7, type=INT], right=NodeCost [value=5, type=INT]]], right=NodeCost [value=6, type=INT]]]"),
        		("La struttura a cascata non e' stata costruita correttamente"));
        
    }
}