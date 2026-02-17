package test;

import static org.junit.jupiter.api.Assertions.*;
import scanner.Scanner;
import parser.Parser;
import ast.NodeProgram;
import visitor.TipoTD;
import visitor.TypeCheckingVisitor;
import visitor.TypeDescriptor;
import visitor.CodeGeneration;
import symbolTable.SymbolTable;
import org.junit.jupiter.api.Test;

class TestCodeGenerator {

	@Test
	void testGenerazioneCodice() throws Exception {
	    String fileName = "2_divsioni.txt"; 
	    
	    // 1. Pipeline iniziale: Scanner -> Parser -> AST
	    Scanner sc = new Scanner("src/test/data/CodeGenerator/" + fileName);
	    Parser p = new Parser(sc);
	    NodeProgram nP = p.parse();

	    // 2. Analisi Semantica (Obbligatoria per decorare l'AST)
	    TypeDescriptor td = new TypeDescriptor(TipoTD.OK);
	    TypeCheckingVisitor tcVisit = new TypeCheckingVisitor(td);
	    nP.accept(tcVisit);
	    
	    // Procedi solo se non ci sono errori semantici
	    if (!td.isError()) {
	        // 3. Generazione del Codice
	        CodeGeneration cgVisit = new CodeGeneration();
	        nP.accept(cgVisit);
	        
	        // 4. Verifica dell'output
	        String codiceProdotto = cgVisit.getCodice();
	        System.out.println("Codice generato per " + fileName + ":\n" + codiceProdotto);
	        
	        // Qui puoi aggiungere asserzioni sulla stringa generata
	        assertNotNull(codiceProdotto);
	    }
	}

}
