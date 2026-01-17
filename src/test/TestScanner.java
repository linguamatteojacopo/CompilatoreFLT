package test;

import scanner.*;
import token.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

class TestScanner {

	@Test
	void testKeyWords() throws Exception {
		Scanner sc = new Scanner("src/test/data/testScanner/testIdKeyWords.txt");
		assertEquals(TokenType.TYINT, sc.nextToken().getType());
		assertEquals(TokenType.ID, sc.nextToken().getType());
		assertEquals(TokenType.TYFLOAT, sc.nextToken().getType());
		assertEquals(TokenType.PRINT, sc.nextToken().getType());
		assertEquals(TokenType.ID, sc.nextToken().getType());
		assertEquals(TokenType.ID, sc.nextToken().getType());
		assertEquals(TokenType.TYINT, sc.nextToken().getType());
		assertEquals(TokenType.ID, sc.nextToken().getType());

	}

	@Test
	void peekToken() throws FileNotFoundException, LexicalException {
		Scanner s = new Scanner("src/test/data/testScanner/testGenerale.txt");
		assertEquals(s.peekToken().getType(), TokenType.TYINT);
		assertEquals(s.nextToken().getType(), TokenType.TYINT);
		assertEquals(s.peekToken().getType(), TokenType.ID);
		assertEquals(s.peekToken().getType(), TokenType.ID);
		Token t = s.nextToken();
		assertEquals(t.getType(), TokenType.ID);
		assertEquals(t.getRiga(), 1);
		assertEquals(t.getValore(), "temp");
	}

	@Test
	void testInt() throws FileNotFoundException, LexicalException {
		Scanner s = new Scanner("src/test/data/testScanner/testInt.txt");
		assertEquals(TokenType.INT, s.nextToken().getType());
		assertEquals(TokenType.INT, s.nextToken().getType());
		assertEquals(TokenType.INT, s.nextToken().getType());
		assertEquals(TokenType.INT, s.nextToken().getType());
	}

	void testFloat() throws FileNotFoundException, LexicalException {
		Scanner s = new Scanner("src/test/data/testScanner/testFloat.txt");
		assertEquals(TokenType.FLOAT, s.nextToken().getType());
		assertEquals(TokenType.FLOAT, s.nextToken().getType());
		assertEquals(TokenType.FLOAT, s.nextToken().getType());
		assertEquals(TokenType.FLOAT, s.nextToken().getType());
	}

}
