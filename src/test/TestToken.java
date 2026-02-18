package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import token.Token;
import token.TokenType;

class TestToken {

	@Test
	void testTokenConValore() {
	    // Creazione di un token ID: <ID, riga 1, "tempa">
	    Token t = new Token(TokenType.ID, 1, "tempa");
	    
	    // Verifica dei getter 
	    assertEquals(TokenType.ID, t.getType());
	    assertEquals(1, t.getRiga());
	    assertEquals("tempa", t.getValore());
	}
	
	@Test
	void testTokenSenzaValore() {
	    // Creazione di un token per il punto e virgola: <SEMI, riga 5>
	    Token t = new Token(TokenType.SEMI, 5);
	    
	    assertEquals(TokenType.SEMI, t.getType());
	    assertEquals(5, t.getRiga());
	    assertNull(t.getValore()); // Il valore dovrebbe essere nullo o non impostato 
	}
	
	@Test
	void testToString() {
	    // Test per token con valore
	    Token t1 = new Token(TokenType.ID, 1, "tempa");
	    String s1 = t1.toString();
	    // Verifica che la stringa contenga le informazioni minime richieste 
	    assertTrue(s1.contains("ID"));
	    assertTrue(s1.contains("1"));
	    assertTrue(s1.contains("tempa"));

	    // Test per token senza valore (es. TYINT)
	    Token t2 = new Token(TokenType.TYINT, 2);
	    String s2 = t2.toString();
	    assertTrue(s2.contains("TYINT"));
	    assertTrue(s2.contains("2"));
	}

}
