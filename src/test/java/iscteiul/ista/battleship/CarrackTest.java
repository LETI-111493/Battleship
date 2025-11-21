package iscteiul.ista.battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;



import org.junit.jupiter.api.*; // Importa tudo, incluindo @Nested e @DisplayName

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests for Carrack (Nau) Class")
class CarrackTest {

    private Carrack carrack;
    private IPosition initialPos;

    @BeforeEach
    void setUp() {
        // Carrack vertical (NORTH) a partir de (3,5) → (3,5), (4,5), (5,5)
        initialPos = new Position(3, 5);
        carrack = new Carrack(Compass.NORTH, initialPos);
    }

    @AfterEach
    void tearDown() {
        carrack = null;
        initialPos = null;
    }

    // -------------------------------------------------------------

    @Nested
    @DisplayName("A. Constructor and Initialization Tests (Path/Branch Coverage)")
    class ConstructorTests {

        // --- Construtores de Sucesso (Cobre os 4 ramos do switch de construção) ---

        @Test
        @DisplayName("Constructor initializes positions correctly when bearing is NORTH")
        void constructorInitializesPositionsNorth() {
            // Teste já existente (Base)
            List<IPosition> positions = carrack.getPositions();
            assertEquals(3, positions.size());
            assertEquals(new Position(5, 5), positions.get(2), "Erro: A terceira posição (NORTH) deveria ser (5,5).");
        }

        @Test
        @DisplayName("Constructor initializes positions correctly when bearing is SOUTH")
        void constructorInitializesPositionsSouth() {
            // NOVO TESTE: Cobre o ramo SOUTH
            Carrack southCarrack = new Carrack(Compass.SOUTH, new Position(6, 6));
            List<IPosition> positions = southCarrack.getPositions();

            // SOUTH (row + 0, 1, 2) a partir de (6,6)
            assertEquals(3, positions.size());
            assertEquals(new Position(8, 6), positions.get(2),
                    "Erro: A terceira posição para SOUTH deveria ser (8,6).");
        }

        @Test
        @DisplayName("Constructor initializes positions correctly when bearing is EAST")
        void constructorInitializesPositionsEast() {
            // Teste já existente (Base)
            Carrack eastCarrack = new Carrack(Compass.EAST, new Position(2, 2));
            List<IPosition> eastPos = eastCarrack.getPositions();
            assertEquals(3, eastPos.size());
            assertEquals(new Position(2, 4), eastPos.get(2),
                    "Erro: A terceira posição para EAST deveria ser (2,4).");
        }

        @Test
        @DisplayName("Constructor initializes positions correctly when bearing is WEST")
        void constructorInitializesPositionsWest() {
            // NOVO TESTE: Cobre o ramo WEST
            Carrack westCarrack = new Carrack(Compass.WEST, new Position(4, 8));
            List<IPosition> positions = westCarrack.getPositions();

            // WEST (col + 0, 1, 2) a partir de (4,8)
            assertEquals(3, positions.size());
            assertEquals(new Position(4, 10), positions.get(2),
                    "Erro: A terceira posição para WEST deveria ser (4,10).");
        }

        // --- Testes de Falha (Cobre caminhos de exceção) ---

        @Test
        @DisplayName("Ship.buildShip() check")
        void buildShipTest() {
            Ship built = Ship.buildShip("nau", Compass.EAST, new Position(1, 1));
            assertNotNull(built);
            assertEquals("Nau", built.getCategory());
            assertEquals(3, built.getSize());
        }

        @Test
        @DisplayName("Constructor throws AssertionError for null bearing")
        void constructorNullBearingThrows() {
            // Cobre o caminho de falha na pré-condição (bearing == null)
            assertThrows(AssertionError.class,
                    () -> new Carrack(null, new Position(0, 0)),
                    "Erro: Construtor com bearing null deveria lançar AssertionError.");
        }

        @Test
        @DisplayName("Constructor throws AssertionError for null position")
        void constructorNullPositionThrows() {
            // NOVO TESTE: Cobre o caminho de falha na pré-condição (pos == null)
            assertThrows(AssertionError.class,
                    () -> new Carrack(Compass.NORTH, null),
                    "Erro: Construtor com posição inicial null deveria lançar AssertionError.");
        }
    }

    // -------------------------------------------------------------

    @Nested
    @DisplayName("B. State and Movement Logic Tests")
    class StateAndLogicTests {

        @Test
        @DisplayName("stillFloating covers all paths (0, 1, 2, 3 hits)")
        void stillFloatingTest() {
            // 1. Caminho (Path): 0 hits -> true (Loop completo, condição not met)
            assertTrue(carrack.stillFloating());

            // 2. Caminho (Path): 1 hit -> true (Loop continua, condição met, mas retorna true)
            carrack.shoot(new Position(3, 5));
            assertTrue(carrack.stillFloating());

            // 3. Caminho (Path): 2 hits -> true (Loop continua, condição met, mas retorna true)
            carrack.shoot(new Position(4, 5));
            assertTrue(carrack.stillFloating());

            // 4. Caminho (Path): 3 hits -> false (Loop completa, retorna false)
            carrack.shoot(new Position(5, 5));
            assertFalse(carrack.stillFloating());
        }

        @Test
        void getTopMostPosTest() {
            assertEquals(3, carrack.getTopMostPos());
        }

        @Test
        void getBottomMostPosTest() {
            assertEquals(5, carrack.getBottomMostPos());
        }

        @Test
        void getLeftMostPosTest() {
            assertEquals(5, carrack.getLeftMostPos());
        }

        @Test
        void getRightMostPosTest() {
            assertEquals(5, carrack.getRightMostPos());
        }
    }

    // -------------------------------------------------------------

    @Nested
    @DisplayName("C. Collision and Interaction Tests")
    class CollisionTests {

        @Test
        void occupiesTest() {
            assertTrue(carrack.occupies(new Position(3, 5)));
            assertFalse(carrack.occupies(new Position(0, 0)));
            assertThrows(AssertionError.class, () -> carrack.occupies(null));
        }

        @Test
        void tooCloseToPositionTest() {
            assertTrue(carrack.tooCloseTo(new Position(3, 6)), "Posição adjacente");
            assertFalse(carrack.tooCloseTo(new Position(10, 10)), "Posição distante");
            assertThrows(NullPointerException.class, () -> carrack.tooCloseTo((IPosition) null));
        }


        @Test
        void tooCloseToShipTest() {
            Ship near = new Carrack(Compass.NORTH, new Position(3, 6));
            Ship far = new Carrack(Compass.NORTH, new Position(10, 10));

            // Caminho True (adjacente, loop breaks early)
            assertTrue(carrack.tooCloseTo(near));
            // Caminho False (distante, loop completes)
            assertFalse(carrack.tooCloseTo(far));
            // Caminho de exceção
            assertThrows(AssertionError.class, () -> carrack.tooCloseTo((Ship) null));
        }

        @Test
        void shootTest() {
            // 1. Caminho: tiro falhado (loop completa)
            carrack.shoot(new Position(0, 0));
            assertTrue(carrack.stillFloating());

            // 2. Caminho: tiro certeiro (loop interrompido após encontrar alvo)
            carrack.shoot(new Position(3, 5));
            assertTrue(carrack.stillFloating());

            // 3. Caminho: exceção
            assertThrows(AssertionError.class, () -> carrack.shoot(null));
        }
    }

    // --- Outras verificações originais agrupadas por conveniência ---

    @Test
    void getCategoryTest() {
        assertEquals("Nau", carrack.getCategory());
    }

    @Test
    void getPositionsTest() {
        assertEquals(3, carrack.getPositions().size());
    }

    @Test
    void getPositionTest() {
        assertEquals(initialPos, carrack.getPosition());
    }

    @Test
    void getBearingTest() {
        assertEquals(Compass.NORTH, carrack.getBearing());
    }

    @Test
    void testToStringTest() {
        assertTrue(carrack.toString().contains("Nau"));
    }

    @Test
    void getSizeTest() {
        assertEquals(3, carrack.getSize());
    }
}