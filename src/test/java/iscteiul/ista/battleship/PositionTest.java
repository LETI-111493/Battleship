package iscteiul.ista.battleship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes da classe Position")
class PositionTest {

    private Position pos;

    @BeforeEach
    void setUp() {
        pos = new Position(2, 3);
    }

    @Test
    @DisplayName("getRow devolve a linha correta")
    void getRow() {
        assertEquals(2, pos.getRow());
    }

    @Test
    @DisplayName("getColumn devolve a coluna correta")
    void getColumn() {
        assertEquals(3, pos.getColumn());
    }

    @Test
    @DisplayName("toString tem o formato esperado")
    void toStringFormat() {
        assertEquals("Linha = 2 Coluna = 3", pos.toString());
    }

    @Test
    @DisplayName("occupy altera o estado isOccupied")
    void occupyChangesState() {
        assertFalse(pos.isOccupied());
        pos.occupy();
        assertTrue(pos.isOccupied());
    }

    @Test
    @DisplayName("shoot altera o estado isHit")
    void shootChangesState() {
        assertFalse(pos.isHit());
        pos.shoot();
        assertTrue(pos.isHit());
    }

    @Test
    @DisplayName("equals com a mesma instância é true")
    void equalsSameInstance() {
        assertTrue(pos.equals(pos));
    }

    @Test
    @DisplayName("equals com mesmas coordenadas é true")
    void equalsSameCoordinates() {
        Position other = new Position(2, 3);
        assertTrue(pos.equals(other));
        assertTrue(other.equals(pos));
    }

    @Test
    @DisplayName("equals com linha diferente é false")
    void notEqualsDifferentRow() {
        Position other = new Position(1, 3);
        assertFalse(pos.equals(other));
    }

    @Test
    @DisplayName("equals com coluna diferente é false")
    void notEqualsDifferentColumn() {
        Position other = new Position(2, 4);
        assertFalse(pos.equals(other));
    }

    @Test
    @DisplayName("equals com null é false")
    void equalsNullIsFalse() {
        assertFalse(pos.equals(null));
    }

    @Test
    @DisplayName("equals com objeto de outro tipo é false")
    void equalsOtherTypeIsFalse() {
        Object other = new Object();
        assertFalse(pos.equals(other));
    }

    @Test
    @DisplayName("isAdjacentTo devolve true para vizinhos imediatos")
    void isAdjacentToNeighbours() {
        assertTrue(pos.isAdjacentTo(new Position(2, 3))); // mesma posição
        assertTrue(pos.isAdjacentTo(new Position(1, 3))); // cima
        assertTrue(pos.isAdjacentTo(new Position(3, 3))); // baixo
        assertTrue(pos.isAdjacentTo(new Position(2, 4))); // direita
        assertTrue(pos.isAdjacentTo(new Position(3, 4))); // diagonal
    }

    @Test
    @DisplayName("isAdjacentTo devolve false para posições afastadas")
    void isAdjacentToFarPositions() {
        assertFalse(pos.isAdjacentTo(new Position(4, 3)));
        assertFalse(pos.isAdjacentTo(new Position(0, 0)));
    }

    @Test
    @DisplayName("isAdjacentTo(null) lança NullPointerException")
    void isAdjacentToNullThrows() {
        assertThrows(NullPointerException.class, () -> pos.isAdjacentTo(null));
    }
}
