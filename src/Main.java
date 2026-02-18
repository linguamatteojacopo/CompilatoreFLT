import scanner.Scanner;
import parser.Parser;
import ast.NodeProgram;
import visitor.TypeCheckingVisitor;
import visitor.CodeGeneration; 
import visitor.TypeDescriptor;
import visitor.TipoTD;
import symbolTable.SymbolTable;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        // 1. Percorso del file sorgente ac
        String filePath = "src/test/data/CodeGenerator/2_divsioni.txt"; 

        try {
            System.out.println("--- Inizio Compilazione ---");

            // 2. Inizializzazione Symbol Table [4, 5]
            SymbolTable.init();

            // 3. Analisi Lessicale e Sintattica per la costruzione dell'AST [6, 7]
            Scanner sc = new Scanner(filePath);
            Parser p = new Parser(sc);
            NodeProgram nP = p.parse(); // Restituisce la radice dell'AST [8, 9]

            // 4. Analisi Semantica (Type Checking) [10, 11]
            // Usiamo il costruttore che richiede TypeDescriptor come indicato nella nostra discussione
            TypeDescriptor td = new TypeDescriptor(TipoTD.OK);
            TypeCheckingVisitor tcVisit = new TypeCheckingVisitor(td);
            nP.accept(tcVisit); // Avvia la visita per il controllo tipi [3, 12]

            // 5. Controllo errori semantici [13, 14]
            if (td.isError()) {
                System.err.println("Errore Semantico rilevato:");
                System.err.println(td.getmsg());
            } else {
                // 6. Generazione del Codice dc [15, 16]
                CodeGeneration cgVisit = new CodeGeneration();
                nP.accept(cgVisit); // Avvia la visita per la generazione codice [17]

                // 7. Output del codice target [18, 19]
                System.out.println("==================================");
                System.out.println("Compilazione completata con successo.");
                System.out.println("Codice DC generato:");
                System.out.println("==================================");
                System.out.println(cgVisit.getCodice());
                System.out.println("==================================");
                System.out.println("Puoi eseguire questo codice in un terminale con il comando 'dc'.");
                System.out.println("SYMBOL TABLE:");
                System.out.println("==================================");
                System.out.println(symbolTable.SymbolTable.toStr());
                System.out.println("==================================");
                
            }

        } catch (FileNotFoundException e) {
            System.err.println("Errore: File non trovato - " + e.getMessage());
        } catch (Exception e) {
            // Gestione eccezioni lessicali e sintattiche [20, 21]
            System.err.println("Errore durante la compilazione: " + e.getMessage());
            e.printStackTrace();
        }
    }
}