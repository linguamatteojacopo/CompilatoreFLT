import scanner.Scanner;
import parser.Parser;
import ast.NodeProgram;
import visitor.TypeCheckingVisitor;
import visitor.CodeGeneration; 
import visitor.TypeDescriptor;
import visitor.TipoTD;
import symbolTable.SymbolTable;

import java.io.FileNotFoundException;
import java.io.PrintWriter; 

public class Main {
    public static void main(String[] args) {
        java.util.Scanner tastiera = new java.util.Scanner(System.in);

        System.out.println("--- Compilatore AC to DC ---");
        System.out.print("Inserisci il percorso del file sorgente: ");
        String filePath = "input ac/"+tastiera.nextLine();

        System.out.print("Inserisci il nome del file di output (es. out.dc): ");
        String outputFileName = tastiera.nextLine();

        try {
            System.out.println("\n--- Inizio Compilazione ---");
            //inizializzo la Symbol Table
            SymbolTable.init();

            // Analisi Lessicale 
            Scanner sc = new Scanner(filePath);
            
            // Analisi Sintattica
            Parser p = new Parser(sc);
            NodeProgram nP = p.parse(); 

            // Analisi Semantica
            TypeDescriptor td = new TypeDescriptor(TipoTD.OK);
            TypeCheckingVisitor tcVisit = new TypeCheckingVisitor(td);
            nP.accept(tcVisit);

            if (td.isError()) {
                System.err.println("Errore Semantico rilevato: " + td.getmsg());
            } else {
                // Generazione Codice
                CodeGeneration cgVisit = new CodeGeneration();
                nP.accept(cgVisit);

                // Scrittura su file del codice generato
                try (PrintWriter writer = new PrintWriter(outputFileName)) {
                    writer.print(cgVisit.getCodice());
                    System.out.println("==================================");
                    System.out.println("Codice Dc Generato");
                    System.out.println(cgVisit.getCodice());
                    System.out.println("==================================");
                    System.out.println("Successo! Codice salvato in: " + outputFileName);
                    System.out.println("==================================");
                }
                
                System.out.println("SYMBOL TABLE:");
                System.out.println(symbolTable.SymbolTable.toStr());
            }

        } catch (FileNotFoundException e) {
            System.err.println("Errore: File non trovato - " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Errore critico: " + e.getMessage());
            e.printStackTrace();
        } finally {
            tastiera.close();
        }
    }
}