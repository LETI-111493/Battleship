package iscteiul.ista.battleship;

import org.junit.jupiter.api.*; // Importa tudo, incluindo @Nested e @DisplayName

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests for Frigate Class (Path/Branch Coverage)")
class FrigateTest {

    private Frigate frigate;
    private Position initialPos = new Position(1, 2);

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
        frigate = new Frigate(Compass.NORTH, initialPos);
    }

    @AfterEach
    void tearDown() {
        frigate = null;
    }

    // -------------------------------------------------------------

    @Nested
    @DisplayName("A. Constructor and Position Paths")
    class ConstructorPaths {

        // --- Construtores de Sucesso (Cobre os 4 ramos do switch de construção) ---

        @Test
        @DisplayName("Path: NORTH - Positions check (Row/Col logic)")
        void testPositionsWhenNorth() {
            List<IPosition> pos = frigate.getPositions();
            assertEquals(4, pos.size());
            // Verifica se o loop de construção (row++) correu 4 vezes (Path)
            assertEquals(4, pos.get(3).getRow());
            assertEquals(2, pos.get(3).getColumn());
        }

        @Test
        @DisplayName("Path: SOUTH - Positions check")
        void testPositionsWhenSouth() {
            Frigate s = new Frigate(Compass.SOUTH, new Position(3, 4));
            // Verifica se o ramo SOUTH no construtor foi executado
            assertEquals(4, s.getPositions().size());
            assertEquals(6, s.getPositions().get(3).getRow());
        }

        @Test
        @DisplayName("Path: EAST - Positions check")
        void testPositionsWhenEast() {
            Frigate e = new Frigate(Compass.EAST, new Position(5, 5));
            // Verifica se o ramo EAST no construtor foi executado
            assertEquals(4, e.getPositions().size());
            assertEquals(8, e.getPositions().get(3).getColumn());
        }

        @Test
        @DisplayName("Path: WEST - Positions check")
        void testPositionsWhenWest() {
            Frigate w = new Frigate(Compass.WEST, new Position(7, 2));
            // Verifica se o ramo WEST no construtor foi executado
            assertEquals(4, w.getPositions().size());
            assertEquals(5, w.getPositions().get(3).getColumn());
        }

        // --- Caminhos de Exceção ---

        @Test
        @DisplayName("Path: Constructor failure for null bearing (Assertion)")
        void testConstructorWithNullBearingThrowsAssertionError() {
            // Cobre o caminho de falha na pré-condição (assert)
            assertThrows(AssertionError.class,
                    () -> new Frigate(null, new Position(0, 0)),
                    "Esperava AssertionError ao criar Frigate com bearing null");
        }

        @Test
        @DisplayName("Path: Constructor failure for null position (Assertion)")
        void testConstructorWithNullPositionThrowsAssertionError() {
            // NOVO TESTE: Cobre o caminho de falha na pré-condição (posição null)
            assertThrows(AssertionError.class,
                    () -> new Frigate(Compass.NORTH, null),
                    "Esperava AssertionError ao criar Frigate com posição null");
        }
    }

    // -------------------------------------------------------------

    @Nested
    @DisplayName("B. State, Collision and Loop Paths")
    class CollisionAndLoopPaths {

        @Test
        @DisplayName("Path 1: stillFloating - Loop continues (some hit)")
        void testStillFloating_SomeUnhit() {
            // Atinge 1 de 4. O loop stillFloating() deve continuar e retornar true (Branch True)
            frigate.shoot(frigate.getPositions().get(0));
            assertTrue(frigate.stillFloating(), "Deveria flutuar após 1 acerto");
        }

        @Test
        @DisplayName("Path 2: stillFloating - Loop completes (all hit)")
        void testStillFloating_AllHit() {
            // Atinge todos os 4. O loop stillFloating() deve completar e retornar false (Branch False)
            for (IPosition p : frigate.getPositions()) {
                p.shoot();
            }
            assertFalse(frigate.stillFloating(), "Deveria afundar após 4 acertos");
        }

        @Test
        @DisplayName("Path 3: tooCloseToShip - Loop break (adjacent)")
        void testTooCloseToShip_Adjacent() {
            // Navio adjacente: forçamos o loop a quebrar cedo (return true)
            Frigate near = new Frigate(Compass.NORTH, new Position(1, 3)); // Adjacente a (1,2)
            assertTrue(frigate.tooCloseTo(near), "Esperava tooCloseTo retornar true");
        }

        @Test
        @DisplayName("Path 4: tooCloseToShip - Loop complete (distant)")
        void testTooCloseToShip_Distant() {
            // Navio distante: forçamos o loop a completar sem encontrar adjacência (return false)
            Frigate far = new Frigate(Compass.NORTH, new Position(10, 10));
            assertFalse(frigate.tooCloseTo(far), "Esperava tooCloseTo retornar false");
        }

        @Test
        @DisplayName("Path 5: shoot - Hit position (loop break)")
        void testShoot_Hit() {
            // Atinge a primeira posição (loop quebra)
            frigate.shoot(frigate.getPositions().get(0));
            // Verifique o estado da posição (se for possível no seu IPosition/Position)
            // Se não for possível, basta o testStillFloating_AllHit que confirma o acerto.
        }

        @Test
        @DisplayName("Path 6: shoot - Miss position (loop complete)")
        void testShoot_Miss() {
            // Tenta atirar numa posição que não pertence ao navio (loop completa sem sucesso)
            frigate.shoot(new Position(9, 9));
            // A fragata ainda deve estar flutuando
            assertTrue(frigate.stillFloating(), "Tiro falhado não deve afundar");
        }
    }

    // -------------------------------------------------------------

    @Nested
    @DisplayName("C. Miscellaneous and Contract Paths")
    class MiscPaths {

        @Test
        void testGetSize() {
            assertEquals(Integer.valueOf(4), frigate.getSize());
        }

        @Test
        void testGetCategory() {
            assertEquals("Fragata", frigate.getCategory());
        }

        @Test
        void testOccupiesAndGetPositionHelpers() {
            List<IPosition> positions = frigate.getPositions();
            IPosition first = positions.get(0);
            IPosition outside = new Position(99, 99);
            assertTrue(frigate.occupies(first), "Esperado occupies() retornar true");
            assertFalse(frigate.occupies(outside), "Esperado occupies() retornar false");
        }

        @Test
        void testCloneThrowsCloneNotSupportedException() {
            FrigateExposer e = new FrigateExposer(Compass.NORTH, new Position(0, 0));
            assertThrows(CloneNotSupportedException.class,
                    () -> e.callClone());
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
            });
        }
    }
}