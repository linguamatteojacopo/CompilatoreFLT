package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import scanner.Scanner;
import parser.Parser;
import ast.NodeProgram;
import visitor.TypeCheckingVisitor;
import visitor.CodeGeneration;
import visitor.TypeDescriptor;
import visitor.TipoTD;
import symbolTable.SymbolTable;

class TestCodeGenerator {

    @BeforeEach
    void setUp() {
        SymbolTable.init(); // Reset della tabella prima di ogni test [1]
    }

    private CodeGeneration runPipeline(String fileRelativePath) throws Exception {
        Scanner sc = new Scanner(fileRelativePath);
        Parser p = new Parser(sc);
        NodeProgram nP = p.parse();

        // Fase 1: Type Checking (necessaria per decorare l'AST e mappare i registri) [2, 3]
        TypeDescriptor td = new TypeDescriptor(TipoTD.OK);
        TypeCheckingVisitor tcVisit = new TypeCheckingVisitor(td);
        nP.accept(tcVisit);

        // Fase 2: Generazione Codice [4]
        CodeGeneration cgVisit = new CodeGeneration();
        nP.accept(cgVisit);
        return cgVisit;
    }

    @Test
    void test1Assign() throws Exception {
        CodeGeneration cg = runPipeline("src/test/data/CodeGenerator/1_assign.txt");
        String code = cg.getCodice();
        
        // Verifica che la stringa generata produca '0' (divisione intera 1/6)
        assertTrue(code.contains("1 6 /"), "Manca la divisione intera");
        assertTrue(cg.getLog().isEmpty(), "Il log dovrebbe essere vuoto");
        System.out.println("Test 1_assign.txt: " + code);
    }

    @Test
    void test2Divisioni() throws Exception {
        CodeGeneration cg = runPipeline("src/test/data/CodeGenerator/2_divsioni.txt");
        String code = cg.getCodice();
        
        // Verifica la gestione della precisione float 5 k / 0 k [5, 6]
        assertTrue(code.contains("5 k / 0 k"), "Manca la gestione della precisione float");
        assertTrue(cg.getLog().isEmpty());
        System.out.println("Test 2_divsioni.txt: " + code);
    }

    @Test
    void test3Generale() throws Exception {
        CodeGeneration cg = runPipeline("src/test/data/CodeGenerator/3_generale.txt");
        String code = cg.getCodice();
        
        // Verifica che il codice contenga le operazioni e le stampe previste
        assertTrue(code.contains("p P"), "Mancano le istruzioni di stampa");
        assertTrue(cg.getLog().isEmpty());
        System.out.println("Test 3_generale.txt: " + code);
    }

    @Test
    void test4RegistriFiniti() throws Exception {
        CodeGeneration cg = runPipeline("src/test/data/CodeGenerator/4_registriFiniti.txt");
        
        // Secondo specifiche: errore nel log e codiceDc limitato alla variabile 'a'
        assertFalse(cg.getLog().isEmpty(), "Dovrebbe esserci un errore per registri esauriti");
        assertTrue(cg.getLog().contains("esauriti") || cg.getLog().contains("registri"), "Messaggio log errato");
        
        String code = cg.getCodice();
        // Verifica che contenga solo l'assegnamento e la stampa di 'a'
        assertTrue(code.startsWith(" 6 2 / sa la p P"), "Il codice generato deve contenere solo le operazioni sulla variabile a");
        // Verifica che non siano stati generati registri oltre il limite consentito
        assertFalse(code.contains("sz"), "Il codice non dovrebbe proseguire dopo l'errore nel log");
        
        System.out.println("Test 4_registriFiniti.txt LOG: " + cg.getLog());
        System.out.println("Test 4_registriFiniti.txt CODE: " + code);
    }
}