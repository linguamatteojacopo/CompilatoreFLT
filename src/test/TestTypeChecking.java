package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import scanner.Scanner;
import parser.Parser;
import ast.NodeProgram;
import visitor.TipoTD;
import visitor.TypeCheckingVisitor;
import visitor.TypeDescriptor;
import symbolTable.SymbolTable;
import java.io.FileNotFoundException;

class TestTypeChecking {

    // Metodo di utilità per evitare ripetizioni di codice
    private TypeDescriptor runTest(String fileName) throws Exception {
        // Reset della Symbol Table prima di ogni test per evitare interferenze
        SymbolTable.init(); 
        
        String path = "src/test/data/CheckingVisitor/" + fileName; 
        Scanner sc = new Scanner(path);
        Parser p = new Parser(sc);
        NodeProgram nP = p.parse();
        
        TypeDescriptor td = new TypeDescriptor(TipoTD.OK);
        TypeCheckingVisitor tcVisit = new TypeCheckingVisitor(td);
        nP.accept(tcVisit);
        return tcVisit.getResType();
    }

    @Test
    void testDichiarazioniRipetute() throws Exception {
        // Il file contiene: int a; float a; [1]
        TypeDescriptor res = runTest("1_dicRipetute.txt");
        assertTrue(res.isError(), "Dovrebbe rilevare la doppia dichiarazione di 'a'");
    }

    @Test
    void testIdNonDichiarato() throws Exception {
        // Il file contiene l'uso della variabile 'b' non dichiarata [2]
        TypeDescriptor res = runTest("2_idNonDec.txt");
        assertTrue(res.isError(), "Dovrebbe rilevare che 'b' non è dichiarata");
    }

    @Test
    void testTipoNonCompatibile() throws Exception {
        // Il file tenta di assegnare un float a un int [3]
        TypeDescriptor res = runTest("4_tipoNonCompatibile.txt");
        assertTrue(res.isError(), "Dovrebbe rilevare l'incompatibilità tra int e float");
    }

    @Test
    void testFileCorretti() throws Exception {
        // Test per i file 5, 6 e 7 che sono semanticamente corretti [4-6]
        assertFalse(runTest("5_corretto.txt").isError());
        assertFalse(runTest("6_corretto.txt").isError());
        assertFalse(runTest("7_corretto.txt").isError());
        
        System.out.println("I file corretti sono stati validati con successo.");
    }
}
