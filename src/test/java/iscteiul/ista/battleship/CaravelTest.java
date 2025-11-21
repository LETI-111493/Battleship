// java
// File: `src/test/java/iscteiul/ista/battleship/CaravelTest.java`
package iscteiul.ista.battleship;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes unitários para Caravel (construtor e getSize)")
class CaravelTest {

    private Caravel caravel;

    @BeforeEach
    void setUp() {
        caravel = new Caravel(Compass.NORTH, new Position(4, 2));
    }

    @AfterEach
    void tearDown() {
        caravel = null;
    }

    @Nested
    @DisplayName("Testes do construtor de Caravel")
    class ConstructorTests {

        @Test
        @DisplayName("Construtor com NORTH inicializa sequência de posições corretamente")
        void constructor_north_sequence() {
            int row = 1, col = 4;
            Caravel c = new Caravel(Compass.NORTH, new Position(row, col));

            assertEquals("Caravela", c.getCategory(), "Erro: categoria inesperada.");
            assertEquals(c.getSize().intValue(), c.getPositions().size(), "Erro: tamanho inconsistente.");

            for (int i = 0; i < c.getSize(); i++) {
                IPosition expected = new Position(row + i, col);
                assertEquals(expected, c.getPositions().get(i),
                        "Erro: posição em índice " + i + " inesperada para NORTH.");
            }
        }

        @Test
        @DisplayName("Construtor com SOUTH inicializa sequência de posições corretamente")
        void constructor_south_sequence() {
            int row = 2, col = 3;
            Caravel c = new Caravel(Compass.SOUTH, new Position(row, col));

            assertEquals("Caravela", c.getCategory());
            assertEquals(c.getSize().intValue(), c.getPositions().size());

            for (int i = 0; i < c.getSize(); i++) {
                IPosition expected = new Position(row + i, col);
                assertEquals(expected, c.getPositions().get(i),
                        "Erro: posição em índice " + i + " inesperada para SOUTH.");
            }
        }

        @Test
        @DisplayName("Construtor com EAST inicializa sequência de posições corretamente")
        void constructor_east_sequence() {
            int row = 5, col = 0;
            Caravel c = new Caravel(Compass.EAST, new Position(row, col));

            assertEquals("Caravela", c.getCategory());
            assertEquals(c.getSize().intValue(), c.getPositions().size());

            for (int i = 0; i < c.getSize(); i++) {
                IPosition expected = new Position(row, col + i);
                assertEquals(expected, c.getPositions().get(i),
                        "Erro: posição em índice " + i + " inesperada para EAST.");
            }
        }

        @Test
        @DisplayName("Construtor com WEST inicializa sequência de posições corretamente")
        void constructor_west_sequence() {
            int row = 4, col = 4;
            Caravel c = new Caravel(Compass.WEST, new Position(row, col));

            assertEquals("Caravela", c.getCategory());
            assertEquals(c.getSize().intValue(), c.getPositions().size());

            for (int i = 0; i < c.getSize(); i++) {
                IPosition expected = new Position(row, col + i);
                assertEquals(expected, c.getPositions().get(i),
                        "Erro: posição em índice " + i + " inesperada para WEST.");
            }
        }

        @Test
        @DisplayName("Construtor com bearing nulo deve lançar AssertionError")
        void constructor_nullBearing_throwsNPE() {
            assertThrows(AssertionError.class,
                    () -> new Caravel(null, new Position(1, 1)),
                    "Erro: esperado AssertionError ao passar bearing nulo.");
        }

        @Test
        @DisplayName("Construtor com posição nula deve lançar AssertionError")
        void constructor_nullPosition_throwsNPE() {
            assertThrows(AssertionError.class,
                    () -> new Caravel(Compass.EAST, null),
                    "Erro: esperado AssertionError ao passar pos nula.");
        }
    }

    @Test
    @DisplayName("getSize deve devolver 2 e ser consistente com positions.size()")
    void getSize_consistentWithPositions() {
        assertNotNull(caravel.getSize(), "Erro: getSize() não deve ser null.");
        assertEquals(Integer.valueOf(caravel.getPositions().size()), caravel.getSize(),
                "Erro: getSize() deve corresponder a positions.size().");
        assertEquals(Integer.valueOf(2), caravel.getSize(), "Erro: getSize() inesperado para Caravel.");
    }
}
