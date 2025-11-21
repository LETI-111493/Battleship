// java
package iscteiul.ista.battleship;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FrigateTest {

    private Frigate frigate;

    // Subclasse de apoio para expor super.clone() e super.finalize() sem reflexão
    private static class FrigateExposer extends Frigate {
        FrigateExposer(Compass bearing, Position pos) {
            super(bearing, pos);
        }

        Object callClone() throws CloneNotSupportedException {
            return super.clone();
        }

        void callFinalize() throws Throwable {
            super.finalize();
        }
    }

    @BeforeEach
    void setUp() {
        frigate = new Frigate(Compass.NORTH, new Position(1, 2));
    }

    @AfterEach
    void tearDown() {
        frigate = null;
    }

    @Test
    void testGetSize() {
        assertEquals(Integer.valueOf(4), frigate.getSize(),
                "Esperado tamanho 4 para Frigate; Atual: " + frigate.getSize());
    }

    @Test
    void testGetCategory() {
        assertEquals("Fragata", frigate.getCategory(),
                "Esperado categoria \"Fragata\"; Atual: " + frigate.getCategory());
    }

    @Test
    void testPositionsWhenNorth() {
        List<IPosition> pos = frigate.getPositions();
        assertEquals(4, pos.size(), "Esperado 4 posições quando orientação NORTH; Atual: " + pos.size());
        for (int i = 0; i < pos.size(); i++) {
            int expectedRow = 1 + i;
            int actualRow = pos.get(i).getRow();
            int actualCol = pos.get(i).getColumn();
            assertEquals(expectedRow, actualRow,
                    "Esperado row=" + expectedRow + " para índice " + i + "; Atual: " + actualRow);
            assertEquals(2, actualCol,
                    "Esperado column=2 para todas as posições quando orientação NORTH; Atual: " + actualCol);
        }
    }

    @Test
    void testPositionsWhenSouth() {
        Frigate s = new Frigate(Compass.SOUTH, new Position(3, 4));
        List<IPosition> pos = s.getPositions();
        assertEquals(4, pos.size(), "Esperado 4 posições quando orientação SOUTH; Atual: " + pos.size());
        for (int i = 0; i < pos.size(); i++) {
            int expectedRow = 3 + i;
            int actualRow = pos.get(i).getRow();
            int actualCol = pos.get(i).getColumn();
            assertEquals(expectedRow, actualRow,
                    "Esperado row=" + expectedRow + " para índice " + i + " quando SOUTH; Atual: " + actualRow);
            assertEquals(4, actualCol,
                    "Esperado column=4 para todas as posições quando SOUTH; Atual: " + actualCol);
        }
    }

    @Test
    void testPositionsWhenEast() {
        Frigate e = new Frigate(Compass.EAST, new Position(5, 5));
        List<IPosition> pos = e.getPositions();
        assertEquals(4, pos.size(), "Esperado 4 posições quando orientação EAST; Atual: " + pos.size());
        for (int i = 0; i < pos.size(); i++) {
            int expectedRow = 5;
            int expectedCol = 5 + i;
            int actualRow = pos.get(i).getRow();
            int actualCol = pos.get(i).getColumn();
            assertEquals(expectedRow, actualRow,
                    "Esperado row=" + expectedRow + " para todas as posições quando EAST; Atual: " + actualRow);
            assertEquals(expectedCol, actualCol,
                    "Esperado column=" + expectedCol + " para índice " + i + " quando EAST; Atual: " + actualCol);
        }
    }

    @Test
    void testPositionsWhenWest() {
        Frigate w = new Frigate(Compass.WEST, new Position(7, 2));
        List<IPosition> pos = w.getPositions();
        assertEquals(4, pos.size(), "Esperado 4 posições quando orientação WEST; Atual: " + pos.size());
        for (int i = 0; i < pos.size(); i++) {
            int expectedRow = 7;
            int expectedCol = 2 + i;
            int actualRow = pos.get(i).getRow();
            int actualCol = pos.get(i).getColumn();
            assertEquals(expectedRow, actualRow,
                    "Esperado row=" + expectedRow + " para todas as posições quando WEST; Atual: " + actualRow);
            assertEquals(expectedCol, actualCol,
                    "Esperado column=" + expectedCol + " para índice " + i + " quando WEST; Atual: " + actualCol);
        }
    }

    @Test
    void testConstructorWithNullBearingThrowsAssertionError() {
        // A implementação atualmente usa assert e, com -ea, lança AssertionError em vez de NullPointerException
        AssertionError thrown = assertThrows(AssertionError.class,
                () -> new Frigate(null, new Position(0, 0)),
                "Esperava AssertionError ao criar Frigate com bearing null, mas nenhuma foi lançada");
        assertNotNull(thrown, "Esperado AssertionError não-nulo como resultado; Atual: " + thrown);
    }

    @Test
    void testCloneThrowsCloneNotSupportedException() {
        FrigateExposer e = new FrigateExposer(Compass.NORTH, new Position(0, 0));
        CloneNotSupportedException thrown = assertThrows(CloneNotSupportedException.class,
                () -> e.callClone(),
                "Esperava CloneNotSupportedException ao invocar super.clone() em Frigate; nenhuma exceção foi lançada");
        assertNotNull(thrown, "Esperado CloneNotSupportedException não-nulo; Atual: " + thrown);
    }

    @Test
    void testFinalizeInvocation() {
        FrigateExposer e = new FrigateExposer(Compass.NORTH, new Position(0, 0));
        assertDoesNotThrow(() -> {
            try {
                e.callFinalize();
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }, "Esperava que finalize() não lançasse exceção, mas foi lançada uma (ver causa)");
    }

    @Test
    void testOccupiesAndGetPositionHelpers() {
        List<IPosition> positions = frigate.getPositions();
        IPosition first = positions.get(0);
        IPosition outside = new Position(99, 99);
        assertTrue(frigate.occupies(first),
                "Esperado occupies() retornar true para posição dentro dos limites; Atual: false");
        assertFalse(frigate.occupies(outside),
                "Esperado occupies() retornar false para posição fora dos limites; Atual: true");
    }

    @Nested
    @DisplayName("Estado inicial e propriedades")
    class StateTests {

        @Test
        @DisplayName("getSize e getCategory devolvem valores esperados (boundary para tamanho)")
        void testGetSizeAndCategory() {
            Frigate f = new Frigate(Compass.NORTH, new Position(0, 0));
            assertEquals(Integer.valueOf(4), f.getSize(), "Esperado tamanho 4 para Frigate");
            assertEquals("Fragata", f.getCategory(), "Esperado categoria \"Fragata\"");
        }

        @Test
        @DisplayName("construtor com orientacao vertical (NORTH) produz 4 posicoes em rows consecutivos")
        void testPositionsWhenNorth_BranchVertical() {
            Frigate f = new Frigate(Compass.NORTH, new Position(2, 3));
            assertEquals(4, f.getPositions().size(), "Esperado 4 posicoes para Frigate");
            for (int i = 0; i < 4; i++) {
                assertEquals(2 + i, f.getPositions().get(i).getRow(), "Row incorreto no indice " + i);
                assertEquals(3, f.getPositions().get(i).getColumn(), "Column deveria ser constante para orientacao vertical");
            }
        }

        @Test
        @DisplayName("construtor com orientacao horizontal (EAST) produz 4 posicoes em columns consecutivos")
        void testPositionsWhenEast_BranchHorizontal() {
            Frigate f = new Frigate(Compass.EAST, new Position(5, 5));
            assertEquals(4, f.getPositions().size(), "Esperado 4 posicoes para Frigate");
            for (int i = 0; i < 4; i++) {
                assertEquals(5, f.getPositions().get(i).getRow(), "Row deveria ser constante para orientacao horizontal");
                assertEquals(5 + i, f.getPositions().get(i).getColumn(), "Column incorreto no indice " + i);
            }
        }
    }

    @Nested
    @DisplayName("Comportamento e excecoes")
    class BehaviorTests {

        @Test
        @DisplayName("construtor com bearing null lança AssertionError (implementação atual usa assert)")
        void testConstructorWithNullBearingThrowsAssertionError() {
            assertThrows(AssertionError.class,
                    () -> new Frigate(null, new Position(0, 0)),
                    "Esperado AssertionError ao passar bearing null (implementação atual usa assert)");
        }

        @Test
        @DisplayName("super.clone() lança CloneNotSupportedException (comportamento herdado)")
        void testCloneThrowsCloneNotSupportedException() {
            FrigateExposer e = new FrigateExposer(Compass.NORTH, new Position(0, 0));
            assertThrows(CloneNotSupportedException.class,
                    () -> e.callClone(),
                    "Esperado CloneNotSupportedException ao invocar super.clone()");
        }

        @Test
        @DisplayName("finalize pode ser invocado sem propagar excecao")
        void testFinalizeDoesNotThrow() {
            FrigateExposer e = new FrigateExposer(Compass.NORTH, new Position(0, 0));
            assertDoesNotThrow(() -> {
                try {
                    e.callFinalize();
                } catch (Throwable t) {
                    throw new RuntimeException(t);
                }
            }, "Esperava que finalize() não lançasse exceção");
        }

        @Test
        @DisplayName("occupies devolve true para posicao interna (limite) e false para posicao externa")
        void testOccupiesBoundaryAndOutside() {
            Frigate f = new Frigate(Compass.SOUTH, new Position(1, 1));
            IPosition first = f.getPositions().get(0);
            IPosition last = f.getPositions().get(f.getPositions().size() - 1);
            IPosition outside = new Position(999, 999);

            assertTrue(f.occupies(first), "Esperado occupies true para primeira posicao");
            assertTrue(f.occupies(last), "Esperado occupies true para ultima posicao (limite)");
            assertFalse(f.occupies(outside), "Esperado occupies false para posicao exterior");
        }
    }
}
