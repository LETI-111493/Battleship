package iscteiul.ista.battleship;

import org.junit.jupiter.api.*;

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

    @Nested
    @DisplayName("tooCloseTo(IPosition) - decomposição de A (linha) e B (coluna)")
    class TooCloseToPositionConditions {

        @Test
        @DisplayName("A=true && B=true -> retorna true (adjacente)")
        void aTrue_bTrue_returnsTrue() {
            Carrack c = new Carrack(Compass.NORTH, new Position(3, 5));
            assertTrue(c.tooCloseTo(new Position(3, 6)));
        }

        @Test
        @DisplayName("A=true && B=false -> retorna false (linha próxima, coluna distante)")
        void aTrue_bFalse_returnsFalse() {
            Carrack c = new Carrack(Compass.NORTH, new Position(3, 5));
            assertFalse(c.tooCloseTo(new Position(4, 8)));
        }

        @Test
        @DisplayName("A=false && B=true -> retorna false (linha distante, coluna próxima)")
        void aFalse_bTrue_returnsFalse() {
            Carrack c = new Carrack(Compass.NORTH, new Position(3, 5));
            assertFalse(c.tooCloseTo(new Position(7, 6)));
        }

        @Test
        @DisplayName("A=false && B=false -> retorna false (distante em ambos)")
        void aFalse_bFalse_returnsFalse() {
            Carrack c = new Carrack(Compass.NORTH, new Position(3, 5));
            assertFalse(c.tooCloseTo(new Position(10, 10)));
        }

        @Test
        @DisplayName("pos null -> lança NullPointerException")
        void positionNull_throwsNPE() {
            Carrack c = new Carrack(Compass.NORTH, new Position(3, 5));
            assertThrows(NullPointerException.class, () -> c.tooCloseTo((IPosition) null));
        }
    }

    @Nested
    @DisplayName("tooCloseTo(Ship) - decomposição de 'other != null' e proximidade")
    class TooCloseToShipConditions {

        @Test
        @DisplayName("other == null -> lança AssertionError (condição atómica false)")
        void otherNull_throwsAssertionError() {
            Carrack c = new Carrack(Compass.NORTH, new Position(3, 5));
            assertThrows(AssertionError.class, () -> c.tooCloseTo((Ship) null));
        }

        @Test
        @DisplayName("other != null && existe posição adjacente -> true")
        void otherNotNull_and_adjacentPosition_returnsTrue() {
            Carrack c = new Carrack(Compass.NORTH, new Position(3, 5));
            Ship other = new Carrack(Compass.NORTH, new Position(3, 6));
            assertTrue(c.tooCloseTo(other));
        }

        @Test
        @DisplayName("other != null && nenhuma posição adjacente -> false")
        void otherNotNull_and_noAdjacentPosition_returnsFalse() {
            Carrack c = new Carrack(Compass.NORTH, new Position(3, 5));
            Ship other = new Carrack(Compass.NORTH, new Position(10, 10));
            assertFalse(c.tooCloseTo(other));
        }

        @Test
        @DisplayName("other com múltiplas posições: uma adjacente basta -> true")
        void otherHasMultiplePositions_oneAdjacent_returnsTrue() {
            Carrack c = new Carrack(Compass.NORTH, new Position(3, 5));
            Ship other = new Carrack(Compass.EAST, new Position(2, 6)); // posições horizontais: (2,6),(2,7),(2,8)
            assertTrue(c.tooCloseTo(other));
        }
    }

    @Nested
    @DisplayName("occupies(...) e shoot(...) - decomposição de pos != null e contains/ocupação")
    class OccupiesAndShootConditions {

        @Test
        @DisplayName("occupies: pos == null -> lança AssertionError")
        void occupies_posNull_throwsAssertionError() {
            Carrack c = new Carrack(Compass.NORTH, new Position(3, 5));
            assertThrows(AssertionError.class, () -> c.occupies(null));
        }

        @Test
        @DisplayName("occupies: pos presente -> true; pos ausente -> false")
        void occupies_presentAndAbsent() {
            Carrack c = new Carrack(Compass.NORTH, new Position(3, 5));
            assertTrue(c.occupies(new Position(3, 5)));
            assertFalse(c.occupies(new Position(0, 0)));
        }

        @Test
        @DisplayName("shoot: pos == null -> lança AssertionError")
        void shoot_posNull_throwsAssertionError() {
            Carrack c = new Carrack(Compass.NORTH, new Position(3, 5));
            assertThrows(AssertionError.class, () -> c.shoot(null));
        }

        @Test
        @DisplayName("shoot: pos não ocupada -> miss mantém stillFloating")
        void shoot_miss_keepsFloating() {
            Carrack c = new Carrack(Compass.NORTH, new Position(3, 5));
            boolean before = c.stillFloating();
            c.shoot(new Position(0, 0));
            assertEquals(before, c.stillFloating());
        }

        @Test
        @DisplayName("shoot: acerto parcial e total -> varia stillFloating")
        void shoot_hit_changesFloatingWhenAllHit() {
            Carrack c = new Carrack(Compass.NORTH, new Position(3, 5));
            c.shoot(new Position(3, 5)); // 1/3 -> ainda flutua
            assertTrue(c.stillFloating());
            c.shoot(new Position(4, 5)); // 2/3 -> ainda flutua
            assertTrue(c.stillFloating());
            c.shoot(new Position(5, 5)); // 3/3 -> afunda
            assertFalse(c.stillFloating());
        }
    }

    @Nested
    @DisplayName("stillFloating - decomposição de cenários: vazio / algum não atingido / todos atingidos")
    class StillFloatingDecomposed {

        @Test
        @DisplayName("lista vazia -> false")
        void emptyList_returnsFalse() {
            Ship empty = new Ship("testEmpty", Compass.NORTH, new Position(0, 0)) {
                @Override
                public Integer getSize() {
                    return getPositions().size();
                }
            };
            empty.getPositions().clear();
            assertFalse(empty.stillFloating());
        }

        @Test
        @DisplayName("algum não atingido -> true (apenas 1 de 3 atingido)")
        void someNotHit_returnsTrue() {
            Ship s = new Ship("test", Compass.NORTH, new Position(1, 1)) {
                @Override
                public Integer getSize() {
                    return getPositions().size();
                }
            };
            s.getPositions().clear();
            Position p1 = new Position(1, 1);
            Position p2 = new Position(2, 1);
            Position p3 = new Position(3, 1);
            s.getPositions().add(p1);
            s.getPositions().add(p2);
            s.getPositions().add(p3);

            s.shoot(p1);
            assertTrue(s.stillFloating());
        }

        @Test
        @DisplayName("todos atingidos -> false")
        void allHit_returnsFalse() {
            Ship s = new Ship("test", Compass.NORTH, new Position(1, 1)) {
                @Override
                public Integer getSize() {
                    return getPositions().size();
                }
            };
            s.getPositions().clear();
            Position p1 = new Position(1, 1);
            Position p2 = new Position(2, 1);
            Position p3 = new Position(3, 1);
            s.getPositions().add(p1);
            s.getPositions().add(p2);
            s.getPositions().add(p3);

            s.shoot(p1);
            s.shoot(p2);
            s.shoot(p3);
            assertFalse(s.stillFloating());
        }
    }

    @Nested
    @DisplayName("Fluxo normal e validações simples")
    class NormalFlowTests {

        @Test
        @DisplayName("getCategory, getSize e toString mantêm invariantes")
        void gettersAndToString_invariants() {
            Carrack c = new Carrack(Compass.WEST, new Position(5, 5));
            assertEquals("Nau", c.getCategory());
            assertEquals(3, c.getSize());
            assertTrue(c.toString().contains("Nau"));
        }
    }

    @Test
    @DisplayName("Construtor SOUTH -> cria posições verticais (row, column) e tamanho 3")
    void constructorSouthCreatesVerticalPositions() {
        Carrack c = new Carrack(Compass.SOUTH, new Position(5, 7));
        List<IPosition> pos = c.getPositions();
        assertEquals(3, pos.size(), "A Nau SOUTH deveria ter 3 posições");
        assertEquals(new Position(5, 7), pos.get(0), "primeira posição incorreta para SOUTH");
        assertEquals(new Position(6, 7), pos.get(1), "segunda posição incorreta para SOUTH");
        assertEquals(new Position(7, 7), pos.get(2), "terceira posição incorreta para SOUTH");
    }
}
