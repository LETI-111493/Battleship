
package iscteiul.ista.battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes unitários para Carrack (construtor e getSize)")
class CarrackTest {

    private Carrack carrack;
    private IPosition initialPos;

    @BeforeEach
    void setUp() {
        initialPos = new Position(3, 5);
        carrack = new Carrack(Compass.NORTH, initialPos);
    }

    @AfterEach
    void tearDown() {
        carrack = null;
        initialPos = null;
    }

    @Nested
    @DisplayName("Testes do construtor de Carrack")
    class ConstructorTests {

        @Test
        @DisplayName("Construtor com NORTH inicializa sequência vertical corretamente")
        void constructor_north_initializes() {
            Position init = new Position(3, 5);
            Carrack c = new Carrack(Compass.NORTH, init);

            assertEquals("Nau", c.getCategory(), "Erro: categoria inesperada.");
            assertEquals(Integer.valueOf(3), c.getSize(), "Erro: tamanho inesperado para a Nau.");
            assertEquals(3, c.getPositions().size(), "Erro: número de posições inesperado.");

            assertEquals(new Position(3, 5), c.getPositions().get(0), "Erro: posição[0] inesperada para NORTH.");
            assertEquals(new Position(4, 5), c.getPositions().get(1), "Erro: posição[1] inesperada para NORTH.");
            assertEquals(new Position(5, 5), c.getPositions().get(2), "Erro: posição[2] inesperada para NORTH.");
        }

        @Test
        @DisplayName("Construtor com SOUTH inicializa sequência vertical corretamente")
        void constructor_south_initializes() {
            Position init = new Position(6, 2);
            Carrack c = new Carrack(Compass.SOUTH, init);

            assertEquals("Nau", c.getCategory());
            assertEquals(Integer.valueOf(3), c.getSize());
            assertEquals(3, c.getPositions().size());

            // implementação atual soma linhas para SOUTH também
            assertEquals(new Position(6, 2), c.getPositions().get(0), "Erro: posição[0] inesperada para SOUTH.");
            assertEquals(new Position(7, 2), c.getPositions().get(1), "Erro: posição[1] inesperada para SOUTH.");
            assertEquals(new Position(8, 2), c.getPositions().get(2), "Erro: posição[2] inesperada para SOUTH.");
        }

        @Test
        @DisplayName("Construtor com EAST inicializa sequência horizontal corretamente")
        void constructor_east_initializes() {
            Position init = new Position(2, 2);
            Carrack c = new Carrack(Compass.EAST, init);

            assertEquals("Nau", c.getCategory());
            assertEquals(Integer.valueOf(3), c.getSize());
            assertEquals(3, c.getPositions().size());

            assertEquals(new Position(2, 2), c.getPositions().get(0), "Erro: posição[0] inesperada para EAST.");
            assertEquals(new Position(2, 3), c.getPositions().get(1), "Erro: posição[1] inesperada para EAST.");
            assertEquals(new Position(2, 4), c.getPositions().get(2), "Erro: posição[2] inesperada para EAST.");
        }

        @Test
        @DisplayName("Construtor com WEST inicializa sequência horizontal corretamente")
        void constructor_west_initializes() {
            Position init = new Position(1, 7);
            Carrack c = new Carrack(Compass.WEST, init);

            assertEquals("Nau", c.getCategory());
            assertEquals(Integer.valueOf(3), c.getSize());
            assertEquals(3, c.getPositions().size());

            // implementação atual soma colunas para WEST também
            assertEquals(new Position(1, 7), c.getPositions().get(0), "Erro: posição[0] inesperada para WEST.");
            assertEquals(new Position(1, 8), c.getPositions().get(1), "Erro: posição[1] inesperada para WEST.");
            assertEquals(new Position(1, 9), c.getPositions().get(2), "Erro: posição[2] inesperada para WEST.");
        }

        @Test
        @DisplayName("Construtor com bearing nulo deve lançar AssertionError")
        void constructor_nullBearing_throwsAssertionError() {
            assertThrows(AssertionError.class,
                    () -> new Carrack(null, new Position(0, 0)),
                    "Erro: esperado AssertionError ao passar bearing nulo.");
        }

        @Test
        @DisplayName("Construtor com posição nula deve lançar AssertionError ou NullPointerException dependendo das asserts")
        void constructor_nullPosition_throws() {
            // se Ship.construtor usa assert pos != null => AssertionError; caso contrário pode ser NPE
            assertThrows(Throwable.class,
                    () -> new Carrack(Compass.NORTH, null),
                    "Erro: esperado AssertionError/NPE ao passar posição nula.");
        }
    }

    @Test
    @DisplayName("getSize deve devolver 3 para Carrack e ser consistente com positions.size()")
    void getSize_returnsExpected() {
        assertNotNull(carrack.getSize(), "Erro: getSize() não deve devolver null.");
        assertEquals(Integer.valueOf(carrack.getPositions().size()), carrack.getSize(),
                "Erro: getSize() deve ser igual a positions.size().");
        assertEquals(Integer.valueOf(3), carrack.getSize(), "Erro: getSize() inesperado para Carrack.");
    }
}
