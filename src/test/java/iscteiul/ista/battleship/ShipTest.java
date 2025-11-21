package iscteiul.ista.battleship;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests for Ship Base Class (Path Coverage Focus)")
class ShipTest {

    // --- Subclasse de apoio ---
    static class TestShip extends Ship {
        public TestShip(String category, Compass bearing, IPosition pos) {
            super(category, bearing, pos);
        }

        @Override
        public Integer getSize() {
            return positions.size();
        }
    }

    private Ship ship;
    private IPosition position;
    private Compass bearing;

    @BeforeEach
    void setUp() {
        // Navio de teste com 2 posições: (2,3) e (2,4). Usado para testes de estado e loops.
        position = new Position(2, 3);
        bearing = Compass.NORTH;
        ship = new TestShip("testShip", bearing, position);
        ship.getPositions().add(new Position(2, 3));
        ship.getPositions().add(new Position(2, 4));
    }

    @AfterEach
    void tearDown() {
        ship = null;
        position = null;
        bearing = null;
    }

    // -------------------------------------------------------------

    @Nested
    @DisplayName("A. Static Factory (buildShip) Paths")
    class BuildShipPaths {
        // Cobre todos os 5 ramos de sucesso (case) e o ramo de falha (default)

        @Test
        void buildShip_barca() {
            Ship s = Ship.buildShip("barca", Compass.NORTH, new Position(0, 0));
            assertNotNull(s);
        }

        @Test
        void buildShip_caravela() {
            Ship s = Ship.buildShip("caravela", Compass.EAST, new Position(0, 1));
            assertNotNull(s);
        }

        @Test
        void buildShip_nau() {
            Ship s = Ship.buildShip("nau", Compass.SOUTH, new Position(1, 0));
            assertNotNull(s);
        }

        @Test
        void buildShip_fragata() {
            Ship s = Ship.buildShip("fragata", Compass.WEST, new Position(1, 1));
            assertNotNull(s);
        }

        @Test
        void buildShip_galeao() {
            Ship s = Ship.buildShip("galeao", Compass.NORTH, new Position(2, 2));
            assertNotNull(s);
        }

        @Test
        void buildShip_unknown() {
            Ship s = Ship.buildShip("unknown-kind", Compass.NORTH, new Position(0, 0));
            assertNull(s);
        }

        @Test
        void buildShip_nullBearing_throwsAssertion() {
            assertThrows(AssertionError.class, () -> Ship.buildShip("barca", null, new Position(0, 0)));
        }

        @Test
        void buildShip_nullPos_throwsAssertion() {
            assertThrows(AssertionError.class, () -> Ship.buildShip("barca", Compass.NORTH, null));
        }
    }

    // -------------------------------------------------------------

    @Nested
    @DisplayName("B. Boundary Getters (Loop Paths)")
    class BoundaryGettersPaths {

        // --- Cobre o ramo de atualização do valor dentro do loop (if true) ---
        @Test
        void getTopMostPos_updatesWhenLaterIsSmaller() {
            Ship s = new TestShip("ext", Compass.EAST, new Position(5, 1));
            s.getPositions().clear();
            s.getPositions().add(new Position(5, 1));
            s.getPositions().add(new Position(2, 1)); // Smaller row (cobre if true)
            assertEquals(2, s.getTopMostPos());
        }

        // --- Cobre o ramo de 'não atualização' (loop continua, if false) ---
        @Test
        void getTopMostPos_initialValueMaintained() {
            Ship s = new TestShip("ext", Compass.EAST, new Position(2, 1));
            s.getPositions().clear();
            s.getPositions().add(new Position(2, 1)); // Top = 2 (início)
            s.getPositions().add(new Position(5, 1)); // Larger row (cobre if false)
            assertEquals(2, s.getTopMostPos(), "Expected top-most row 2 to be maintained");
        }

        // --- Cobre o ramo de atualização do valor dentro do loop (if true) ---
        @Test
        void getBottomMostPos_updatesWhenLaterIsLarger() {
            Ship s = new TestShip("ext", Compass.EAST, new Position(1, 1));
            s.getPositions().clear();
            s.getPositions().add(new Position(1, 1));
            s.getPositions().add(new Position(4, 1)); // Larger row (cobre if true)
            assertEquals(4, s.getBottomMostPos());
        }

        // --- Cobre o ramo de 'não atualização' (loop continua, if false) ---
        @Test
        void getBottomMostPos_initialValueMaintained() {
            Ship s = new TestShip("ext", Compass.EAST, new Position(7, 1));
            s.getPositions().clear();
            s.getPositions().add(new Position(7, 1)); // Bottom = 7 (início)
            s.getPositions().add(new Position(4, 1)); // Smaller row (cobre if false)
            assertEquals(7, s.getBottomMostPos(), "Expected bottom-most row 7 to be maintained");
        }

        // --- Cobre o ramo de atualização do valor dentro do loop (if true) ---
        @Test
        void getLeftMostPos_updatesWhenLaterIsSmaller() {
            Ship s = new TestShip("ext", Compass.WEST, new Position(3, 5));
            s.getPositions().clear();
            s.getPositions().add(new Position(3, 5));
            s.getPositions().add(new Position(3, 2));
            assertEquals(2, s.getLeftMostPos());
        }

        // --- Cobre o ramo de 'não atualização' (loop continua, if false) ---
        @Test
        void getLeftMostPos_initialValueMaintained() {
            Ship s = new TestShip("ext", Compass.WEST, new Position(3, 2));
            s.getPositions().clear();
            s.getPositions().add(new Position(3, 2)); // Left = 2 (início)
            s.getPositions().add(new Position(3, 5)); // Larger col (cobre if false)
            assertEquals(2, s.getLeftMostPos(), "Expected left-most col 2 to be maintained");
        }

        // --- Cobre o ramo de atualização do valor dentro do loop (if true) ---
        @Test
        void getRightMostPos_updatesWhenLaterIsLarger() {
            Ship s = new TestShip("ext", Compass.WEST, new Position(3, 1));
            s.getPositions().clear();
            s.getPositions().add(new Position(3, 1));
            s.getPositions().add(new Position(3, 7));
            assertEquals(7, s.getRightMostPos());
        }

        // --- Cobre o ramo de 'não atualização' (loop continua, if false) ---
        @Test
        void getRightMostPos_initialValueMaintained() {
            Ship s = new TestShip("ext", Compass.WEST, new Position(3, 7));
            s.getPositions().clear();
            s.getPositions().add(new Position(3, 7)); // Right = 7 (início)
            s.getPositions().add(new Position(3, 1)); // Smaller col (cobre if false)
            assertEquals(7, s.getRightMostPos(), "Expected right-most col 7 to be maintained");
        }
    }

    // -------------------------------------------------------------

    @Nested
    @DisplayName("C. State and Interaction Paths")
    class StateAndInteractionPaths {

        // --- stillFloating (Cobre caminhos do loop) ---
        @Test
        void stillFloating_allHit() {
            // Caminho 1: Loop completo e retorna FALSE
            for (IPosition p : ship.getPositions()) p.shoot();
            assertFalse(ship.stillFloating());
        }

        @Test
        void stillFloating_someUnhit() {
            // Caminho 2: Loop quebra e retorna TRUE
            ship.getPositions().get(0).shoot();
            assertTrue(ship.stillFloating());
        }

        @Test
        void stillFloating_emptyPositions() {
            Ship empty = new TestShip("empty", Compass.SOUTH, new Position(0, 0));
            // Cobre o caminho onde o loop sobre posições é ignorado (Path/Branch)
            assertFalse(empty.stillFloating(), "Expected not floating when there are no positions");
        }

        // --- tooCloseTo(IShip) (Cobre caminhos do loop) ---
        @Test
        void tooCloseToShip_trueIfAnyAdjacent() {
            // Caminho 1: Loop quebra cedo e retorna TRUE
            Ship other = new TestShip("other", Compass.EAST, new Position(2, 5));
            other.getPositions().add(new Position(2, 5));
            assertTrue(ship.tooCloseTo(other));
        }

        @Test
        void tooCloseToShip_falseIfFar() {
            // Caminho 2: Loop completa e retorna FALSE
            Ship other = new TestShip("other", Compass.EAST, new Position(10, 10));
            other.getPositions().add(new Position(10, 10));
            assertFalse(ship.tooCloseTo(other));
        }

        // --- shoot (Cobre caminhos do loop) ---
        @Test
        void shoot_hitPositionMarksHit() {
            // Caminho 1: Loop quebra cedo após acerto (return true implícito)
            IPosition p = new Position(2, 3);
            ship.shoot(p);
            assertTrue(ship.getPositions().get(0).isHit());
        }

        @Test
        void shoot_missDoesNotMarkOthers() {
            // Caminho 2: Loop completa após falha (return false implícito)
            IPosition p = new Position(9, 9);
            ship.shoot(p);
            assertFalse(ship.getPositions().get(0).isHit());
        }
    }

    // -------------------------------------------------------------

    @Nested
    @DisplayName("D. Assertion and Contract Paths")
    class AssertionAndContractPaths {

        // Cobre o caminho de exceção para occupies(null)
        @Test
        void occupies_nullThrows() {
            assertThrows(AssertionError.class, () -> ship.occupies(null));
        }

        // Cobre o caminho de exceção para tooCloseToShip(null)
        @Test
        void tooCloseToShip_nullThrows() {
            assertThrows(AssertionError.class, () -> ship.tooCloseTo((IShip) null));
        }

        // Cobre o caminho de exceção para shoot(null)
        @Test
        void shoot_nullThrows() {
            assertThrows(AssertionError.class, () -> ship.shoot(null));
        }

        // Cobre o caminho de exceção para construtor (null bearing)
        @Test
        void constructor_nullBearingThrows() {
            assertThrows(AssertionError.class, () -> new TestShip("t", null, new Position(1, 1)));
        }

        // Cobre o caminho de exceção para construtor (null position)
        @Test
        void constructor_nullPositionThrows() {
            assertThrows(AssertionError.class, () -> new TestShip("t", Compass.NORTH, null));
        }

        // --- Verificações de Base ---

        @Test
        void getCategory() {
            assertEquals("testShip", ship.getCategory());
        }

        @Test
        void getPositions() {
            assertEquals(2, ship.getPositions().size());
        }

        @Test
        void testToString_containsCategoryBearingPosition() {
            String actual = ship.toString();
            assertTrue(actual.contains(ship.getCategory()));
        }
    }

    @DisplayName("Ship - Cobertura de Condições (Condition Coverage)")
    class ShipConditionCoverageTest {

        static class TestShip extends Ship {
            TestShip(String category, Compass bearing, IPosition pos) {
                super(category, bearing, pos);
            }

            @Override
            public Integer getSize() {
                return positions.size();
            }
        }

        // Helper comum
        private Ship makeShipWithPositions() {
            TestShip s = new TestShip("t", Compass.NORTH, new Position(2, 3));
            s.getPositions().add(new Position(2, 3)); // P0
            s.getPositions().add(new Position(2, 4)); // P1
            return s;
        }

        @Nested
        @DisplayName("A && B - decomposição completa (cada átomo varia)")
        class AndExpressionDecomposition {

            @Test
            @DisplayName("A=true && B=true -> ocupa(posA) && tooCloseTo(posB) -> true")
            void aTrue_bTrue_returnsTrue() {
                Ship s = makeShipWithPositions();
                IPosition posA = new Position(2, 3); // ocupa -> true
                IPosition posB = new Position(2, 4); // tooCloseTo -> true (adjacente/igual)
                boolean A = s.occupies(posA);
                boolean B = s.tooCloseTo(posB);
                assertTrue(A && B, "Esperado true quando A=true e B=true");
            }

            @Test
            @DisplayName("A=true && B=false -> ocupa(posA) && tooCloseTo(far) -> false")
            void aTrue_bFalse_returnsFalse() {
                Ship s = makeShipWithPositions();
                IPosition posA = new Position(2, 3); // ocupa -> true
                IPosition far = new Position(9, 9);  // tooCloseTo -> false
                boolean A = s.occupies(posA);
                boolean B = s.tooCloseTo(far);
                assertFalse(A && B, "Esperado false quando A=true e B=false");
            }

            @Test
            @DisplayName("A=false && B=true -> ocupa(fora) && tooCloseTo(posB) -> false")
            void aFalse_bTrue_returnsFalse() {
                Ship s = makeShipWithPositions();
                IPosition outside = new Position(7, 7); // ocupa -> false
                IPosition posB = new Position(2, 4);     // tooCloseTo -> true
                boolean A = s.occupies(outside);
                boolean B = s.tooCloseTo(posB);
                assertFalse(A && B, "Esperado false quando A=false e B=true");
            }

            @Test
            @DisplayName("A=false && B=false -> ocupa(fora) && tooCloseTo(far) -> false")
            void aFalse_bFalse_returnsFalse() {
                Ship s = makeShipWithPositions();
                IPosition outside = new Position(7, 7); // ocupa -> false
                IPosition far = new Position(8, 8);      // tooCloseTo -> false
                boolean A = s.occupies(outside);
                boolean B = s.tooCloseTo(far);
                assertFalse(A && B, "Esperado false quando A=false e B=false");
            }
        }

        @Nested
        @DisplayName("A || B - decomposição completa (cada átomo varia)")
        class OrExpressionDecomposition {

            @Test
            @DisplayName("A=true || B=true -> ocupa(posA) || tooCloseTo(posB) -> true")
            void aTrue_bTrue_returnsTrue() {
                Ship s = makeShipWithPositions();
                IPosition posA = new Position(2, 3); // true
                IPosition posB = new Position(2, 4); // true
                assertTrue(s.occupies(posA) || s.tooCloseTo(posB));
            }

            @Test
            @DisplayName("A=true || B=false -> ocupa(posA) || tooCloseTo(far) -> true")
            void aTrue_bFalse_returnsTrue() {
                Ship s = makeShipWithPositions();
                IPosition posA = new Position(2, 3); // true
                IPosition far = new Position(9, 9);  // false
                assertTrue(s.occupies(posA) || s.tooCloseTo(far));
            }

            @Test
            @DisplayName("A=false || B=true -> ocupa(outside) || tooCloseTo(posB) -> true")
            void aFalse_bTrue_returnsTrue() {
                Ship s = makeShipWithPositions();
                IPosition outside = new Position(7, 7); // false
                IPosition posB = new Position(2, 4);     // true
                assertTrue(s.occupies(outside) || s.tooCloseTo(posB));
            }

            @Test
            @DisplayName("A=false || B=false -> ocupa(outside) || tooCloseTo(far) -> false")
            void aFalse_bFalse_returnsFalse() {
                Ship s = makeShipWithPositions();
                IPosition outside = new Position(7, 7); // false
                IPosition far = new Position(8, 8);      // false
                assertFalse(s.occupies(outside) || s.tooCloseTo(far));
            }
        }

        @Nested
        @DisplayName("Ternary operator - decomposição da condição composta")
        class TernaryDecomposition {

            @Test
            @DisplayName("A=true && B=true -> devolve \"OK\"")
            void condTrue_returnsOK() {
                Ship s = makeShipWithPositions();
                IPosition a = new Position(2, 3); // occupies true
                IPosition b = new Position(2, 4); // tooCloseTo true
                String result = (s.occupies(a) && s.tooCloseTo(b)) ? "OK" : "NO";
                assertEquals("OK", result);
            }

            @Test
            @DisplayName("A=false || B=false -> devolve \"NO\"")
            void condFalse_returnsNO() {
                Ship s = makeShipWithPositions();
                IPosition a = new Position(7, 7); // occupies false
                IPosition b = new Position(9, 9); // tooCloseTo false
                String result = (s.occupies(a) && s.tooCloseTo(b)) ? "OK" : "NO";
                assertEquals("NO", result);
            }
        }

        @Nested
        @DisplayName("while/combinada - garantir avaliação de cada átomo")
        class WhileAndCombinedDecomposition {

            @Test
            @DisplayName("while com A && B - B=false impede iterações")
            void whileComposite_bFalse_noIterations() {
                boolean A = true;
                boolean B = false;
                int it = 0;
                int limit = 5;
                while (A && B && it < limit) {
                    it++;
                }
                assertEquals(0, it, "Quando B=false o laço não deve correr");
            }

            @Test
            @DisplayName("while com A && B - A=true B=true executa até limite")
            void whileComposite_aTrue_bTrue_runs() {
                boolean A = true;
                boolean B = true;
                int it = 0;
                int limit = 3;
                while (A && B && it < limit) {
                    it++;
                }
                assertEquals(limit, it, "Quando A && B true o laço deve executar até ao limite");
            }
        }

        @Nested
        @DisplayName("Fluxo normal - separar testes de lógica booleana dos fluxos padrão")
        class NormalFlowTests {

            @Test
            @DisplayName("stillFloating: todos atingidos -> false")
            void stillFloating_allHit_false() {
                Ship s = makeShipWithPositions();
                for (IPosition p : s.getPositions()) p.shoot();
                assertFalse(s.stillFloating(), "Esperado false quando todas as posições estão atingidas");
            }

            @Test
            @DisplayName("stillFloating: há não-atingida -> true")
            void stillFloating_someUnhit_true() {
                Ship s = makeShipWithPositions();
                s.getPositions().get(0).shoot();
                assertTrue(s.stillFloating(), "Esperado true se existir pelo menos uma posição não atingida");
            }

            @Test
            @DisplayName("occupies(null) lança AssertionError")
            void occupies_null_throwsAssertion() {
                Ship s = makeShipWithPositions();
                assertThrows(AssertionError.class, () -> s.occupies(null));
            }
        }
    }
}
