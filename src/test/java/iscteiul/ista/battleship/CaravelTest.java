package iscteiul.ista.battleship;


import org.junit.jupiter.api.*; // Importa tudo, incluindo @Nested e @DisplayName

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests for Caravel Class")
class CaravelTest {

    private Caravel caravel;
    private IPosition initialPos;

    @BeforeEach
    void setUp() {
        // Caravela vertical (NORTH) a partir de (3,5) → (3,5) e (4,5)
        initialPos = new Position(3, 5);
        caravel = new Caravel(Compass.NORTH, initialPos);
    }

    @AfterEach
    void tearDown() {
        caravel = null;
        initialPos = null;
    }

    // -------------------------------------------------------------

    @Nested
    @DisplayName("A. Constructor and Initialization Tests")
    class ConstructorTests {

        // --- Construtores de Sucesso (Cobre os 4 ramos do switch de construção) ---

        @Test
        @DisplayName("Constructor initializes positions correctly when bearing is NORTH")
        void constructorInitializesPositionsNorth() {
            // Teste já existente (Base)
            assertEquals(2, caravel.getPositions().size());
            assertEquals(new Position(4, 5), caravel.getPositions().get(1));
        }

        @Test
        @DisplayName("Constructor initializes positions correctly when bearing is SOUTH")
        void constructorInitializesPositionsSouth() {
            // NOVO TESTE para cobrir explicitamente o ramo SOUTH
            Caravel southCaravel = new Caravel(Compass.SOUTH, new Position(5, 5));
            List<IPosition> southPos = southCaravel.getPositions();

            // SOUTH (row + 0, 1) a partir de (5,5): (5,5) e (6,5)
            assertEquals(2, southPos.size());
            assertEquals(new Position(6, 5), southPos.get(1),
                    "Erro: A segunda posição para SOUTH deveria ser (6,5).");
        }

        @Test
        @DisplayName("Constructor initializes positions correctly when bearing is EAST")
        void constructorInitializesPositionsEast() {
            // Teste já existente (Base)
            Caravel eastCaravel = new Caravel(Compass.EAST, new Position(2, 2));
            List<IPosition> eastPos = eastCaravel.getPositions();
            assertEquals(2, eastPos.size());
            assertEquals(new Position(2, 3), eastPos.get(1));
        }

        @Test
        @DisplayName("Constructor initializes positions correctly when bearing is WEST")
        void constructorInitializesPositionsWest() {
            // NOVO TESTE para cobrir explicitamente o ramo WEST
            Caravel westCaravel = new Caravel(Compass.WEST, new Position(7, 7));
            List<IPosition> westPos = westCaravel.getPositions();

            // WEST (col + 0, 1) a partir de (7,7): (7,7) e (7,8) (assume-se que WEST vai para a direita para simplificar)
            // Se o seu código usa WEST para construir para a esquerda, ajuste a expectativa: (7,7) e (7,6)
            assertEquals(2, westPos.size());
            assertEquals(new Position(7, 8), westPos.get(1),
                    "Erro: A segunda posição para WEST deveria ser (7,8).");
        }

        // --- Testes de Falha (Cobre caminhos de exceção) ---

        @Test
        @DisplayName("Constructor throws AssertionError for null bearing")
        void constructorThrowsExceptionForNullBearing() {
            // Cobre o caminho de falha na pré-condição (bearing == null)
            assertThrows(AssertionError.class,
                    () -> new Caravel(null, new Position(0, 0)),
                    "Erro: new Caravel(null, pos) deveria lançar AssertionError.");
        }

        @Test
        @DisplayName("Constructor throws AssertionError for null position")
        void constructorThrowsExceptionForNullPosition() {
            // NOVO TESTE: Cobre o caminho de falha na pré-condição (pos == null)
            assertThrows(AssertionError.class,
                    () -> new Caravel(Compass.NORTH, null),
                    "Erro: new Caravel(bearing, null) deveria lançar AssertionError.");
        }

        @Test
        @DisplayName("Ship.buildShip() check")
        void buildShipTest() {
            Ship built = Ship.buildShip("caravela", Compass.EAST, new Position(1, 1));
            assertNotNull(built);
            assertEquals("Caravela", built.getCategory());
            assertEquals(2, built.getSize());
        }
    }

    // -------------------------------------------------------------

    @Nested
    @DisplayName("B. Boundary and State Tests")
    class BoundaryAndStateTests {

        // --- Testes de Estado ---

        @Test
        @DisplayName("stillFloating covers all paths (hit, still floating, sunk)")
        void stillFloatingTest() {
            // 1. Ramo: Inicialmente flutuando (true)
            assertTrue(caravel.stillFloating());

            // 2. Ramo: Acertar apenas numa das posições → loop continua e retorna true
            caravel.shoot(initialPos);
            assertTrue(caravel.stillFloating());

            // 3. Ramo: Acertar a segunda posição → loop completa e retorna false
            caravel.shoot(new Position(4, 5));
            assertFalse(caravel.stillFloating());
        }

        // --- Testes de Limites (Bounds) - Caminhos de Lógica Mínima e Máxima ---

        @Test
        void getTopMostPosTest() {
            assertEquals(3, caravel.getTopMostPos(), "Erro: TopMost (min row) deveria ser 3.");
        }

        @Test
        void getBottomMostPosTest() {
            assertEquals(4, caravel.getBottomMostPos(), "Erro: BottomMost (max row) deveria ser 4.");
        }

        @Test
        void getLeftMostPosTest() {
            assertEquals(5, caravel.getLeftMostPos(), "Erro: LeftMost (min col) deveria ser 5.");
        }

        @Test
        void getRightMostPosTest() {
            assertEquals(5, caravel.getRightMostPos(), "Erro: RightMost (max col) deveria ser 5.");
        }
    }

    // -------------------------------------------------------------

    @Nested
    @DisplayName("C. Collision and Interaction Tests")
    class CollisionTests {

        @Test
        void occupiesTest() {
            assertTrue(caravel.occupies(initialPos));
            assertFalse(caravel.occupies(new Position(0, 0)));
            assertThrows(AssertionError.class, () -> caravel.occupies(null));
        }

        @Test
        void tooCloseToPositionTest() {
            assertTrue(caravel.tooCloseTo(new Position(3, 6)), "Posição adjacente");
            assertFalse(caravel.tooCloseTo(new Position(10, 10)), "Posição distante");
            assertThrows(NullPointerException.class, () -> caravel.tooCloseTo((IPosition) null));
        }

        @Test
        void tooCloseToShipTest() {
            Ship near = new Caravel(Compass.NORTH, new Position(3, 6));
            Ship far = new Caravel(Compass.NORTH, new Position(10, 10));

            // Cobre o caminho True (adjacente)
            assertTrue(caravel.tooCloseTo(near), "Barcos adjacentes");
            // Cobre o caminho False (distante)
            assertFalse(caravel.tooCloseTo(far), "Barcos distantes");
            // Cobre o caminho de exceção
            assertThrows(AssertionError.class, () -> caravel.tooCloseTo((Ship) null));
        }

        @Test
        void shootTest() {
            // 1. Caminho: tiro falhado (loop completa, não atinge)
            caravel.shoot(new Position(0, 0));
            assertTrue(caravel.stillFloating());

            // 2. Caminho: tiro certeiro (loop interrompido após encontrar alvo)
            caravel.shoot(initialPos);

            // 3. Caminho: exceção
            assertThrows(AssertionError.class, () -> caravel.shoot(null));
        }
    }

    // -------------------------------------------------------------

    // --- Métodos de Verificação Final ---

    @Test
    void getCategoryTest() {
        assertEquals("Caravela", caravel.getCategory());
    }

    @Test
    void getPositionsTest() {
        assertEquals(2, caravel.getPositions().size());
        assertEquals(new Position(4, 5), caravel.getPositions().get(1));
    }

    @Test
    void getPositionTest() {
        assertEquals(initialPos, caravel.getPosition());
    }

    @Test
    void getBearingTest() {
        assertEquals(Compass.NORTH, caravel.getBearing());
    }

    @Test
    void testToStringTest() {
        String s = caravel.toString();
        assertTrue(s.contains("Caravela"));
    }

    @Test
    void getSizeTest() {
        assertEquals(2, caravel.getSize());
    }
}