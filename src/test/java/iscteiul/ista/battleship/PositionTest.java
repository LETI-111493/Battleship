// java
package iscteiul.ista.battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

    private Position pos;

    @BeforeEach
    void setUp() {
        // cria a instância usada em todos os testes
        pos = new Position(2, 3);
    }

    @AfterEach
    void tearDown() {
        // limpa a instância após cada teste
        pos = null;
    }

    @Test
    void getRow() {
        // Espera-se que getRow retorne o valor passado no construtor (2)
        assertEquals(2, pos.getRow(), "Esperava row = 2 mas foi diferente do esperado");
    }

    @Test
    void getColumn() {
        // Espera-se que getColumn retorne o valor passado no construtor (3)
        assertEquals(3, pos.getColumn(), "Esperava column = 3 mas foi diferente do esperado");
    }

    @Test
    void testHashCode() {
        // Dois objetos com mesmos row/column e mesmos flags devem ter o mesmo hashCode
        Position other = new Position(2, 3);
        assertEquals(pos.hashCode(), other.hashCode(), "Esperava hashCode igual para posições equivalentes mas foram diferentes");

        // Se um dos flags mudar (occupy), o hashCode deve mudar
        int before = pos.hashCode();
        pos.occupy();
        int after = pos.hashCode();
        assertNotEquals(before, after, "Esperava hashCode diferente após occupy() mas permaneceu igual");

        // Se marcar como hit também altera o hashCode (testa outro ramo)
        other = new Position(2, 3);
        int beforeOther = other.hashCode();
        other.shoot();
        int afterOther = other.hashCode();
        assertNotEquals(beforeOther, afterOther, "Esperava hashCode diferente após shoot() mas permaneceu igual");
    }

    @Test
    void testEquals() {
        // Igualdade com a própria referência
        assertTrue(pos.equals(pos), "Esperava equals(true) quando comparado com a própria instância mas retornou false");

        // Igualdade com outra Position com mesmos row/column
        Position same = new Position(2, 3);
        assertTrue(pos.equals(same), "Esperava equals(true) para posições com mesmos row/column mas retornou false");
        assertTrue(same.equals(pos), "Esperava simetria em equals mas a comparação inversa retornou false");

        // Diferentes coordenadas -> false
        Position differentRow = new Position(1, 3);
        assertFalse(pos.equals(differentRow), "Esperava equals(false) para row diferente mas retornou true");

        Position differentColumn = new Position(2, 4);
        assertFalse(pos.equals(differentColumn), "Esperava equals(false) para column diferente mas retornou true");

        // Comparar com null -> false (sem lançar exceção)
        assertFalse(pos.equals(null), "Esperava equals(false) quando comparado com null mas retornou true");

        // Comparar com objeto que NÃO implementa IPosition -> false
        Object notPosition = new Object();
        assertFalse(pos.equals(notPosition), "Esperava equals(false) quando comparado com objeto de outro tipo mas retornou true");

        // Comparar com objeto que IMPLEMENTA IPosition mas não é Position: equals deve usar getRow/getColumn -> true
        IPosition otherIPos = new IPosition() {
            @Override public int getRow() { return 2; }
            @Override public int getColumn() { return 3; }
            @Override public boolean isAdjacentTo(IPosition other) { return false; }
            @Override public void occupy() { /* no-op */ }
            @Override public void shoot() { /* no-op */ }
            @Override public boolean isOccupied() { return false; }
            @Override public boolean isHit() { return false; }
        };
        assertTrue(pos.equals(otherIPos), "Esperava equals(true) quando comparado com IPosition com mesmas coordenadas mas retornou false");
    }

    @Test
    void isAdjacentTo() {
        // Mesmo local -> adjacente
        assertTrue(pos.isAdjacentTo(new Position(2, 3)), "Esperava isAdjacentTo(true) para mesma posição mas retornou false");

        // Vizinhos vertical/horizontal/diagonal devem ser adjacentes
        assertTrue(pos.isAdjacentTo(new Position(1, 3)), "Esperava isAdjacentTo(true) para posição (1,3) mas retornou false");
        assertTrue(pos.isAdjacentTo(new Position(3, 3)), "Esperava isAdjacentTo(true) para posição (3,3) mas retornou false");
        assertTrue(pos.isAdjacentTo(new Position(2, 4)), "Esperava isAdjacentTo(true) para posição (2,4) mas retornou false");
        assertTrue(pos.isAdjacentTo(new Position(3, 4)), "Esperava isAdjacentTo(true) para posição diagonal (3,4) mas retornou false");

        // Posição com distância > 1 não é adjacente
        assertFalse(pos.isAdjacentTo(new Position(4, 3)), "Esperava isAdjacentTo(false) para posição (4,3) mas retornou true");
        assertFalse(pos.isAdjacentTo(new Position(0, 0)), "Esperava isAdjacentTo(false) para posição (0,0) mas retornou true");

        // Passar null deve lançar NullPointerException (outro ramo)
        assertThrows(NullPointerException.class, () -> pos.isAdjacentTo(null),
                "Esperava NullPointerException ao chamar isAdjacentTo(null) mas nenhuma exceção foi lançada");
    }

    @Test
    void occupy() {
        // Inicialmente não ocupado
        assertFalse(pos.isOccupied(), "Esperava isOccupied() == false imediatamente após criação mas retornou true");

        // Depois de occupy() -> isOccupied true
        pos.occupy();
        assertTrue(pos.isOccupied(), "Esperava isOccupied() == true após occupy() mas retornou false");
    }

    @Test
    void shoot() {
        // Inicialmente não atingido
        assertFalse(pos.isHit(), "Esperava isHit() == false imediatamente após criação mas retornou true");

        // Depois de shoot() -> isHit true
        pos.shoot();
        assertTrue(pos.isHit(), "Esperava isHit() == true após shoot() mas retornou false");
    }

    @Test
    void isOccupied() {
        // Verifica ramo padrão false -> true
        assertFalse(pos.isOccupied(), "Esperava isOccupied() inicialmente false mas retornou true");
        pos.occupy();
        assertTrue(pos.isOccupied(), "Esperava isOccupied() true após occupy() mas retornou false");
    }

    @Test
    void isHit() {
        // Verifica ramo padrão false -> true
        assertFalse(pos.isHit(), "Esperava isHit() inicialmente false mas retornou true");
        pos.shoot();
        assertTrue(pos.isHit(), "Esperava isHit() true após shoot() mas retornou false");
    }

    @Test
    void testToString() {
        // Deve corresponder exatamente ao formato implementado em Position.toString()
        String expected = "Linha = 2 Coluna = 3";
        assertEquals(expected, pos.toString(), "Esperava toString() com valor '" + expected + "' mas foi '" + pos.toString() + "'");
    }
}
