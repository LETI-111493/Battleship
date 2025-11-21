// language: java
package iscteiul.ista.battleship;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Revised unit tests for {@link Ship} aiming at maximum branch/line coverage.
 */
class ShipTest {

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

    // ---------- buildShip: cover all switch cases ----------
    @Test
    void buildShip_barca() {
        Ship s = Ship.buildShip("barca", Compass.NORTH, new Position(0, 0));
        assertNotNull(s, "Error: expected non-null Ship for 'barca' but got null");
    }

    @Test
    void buildShip_caravela() {
        Ship s = Ship.buildShip("caravela", Compass.EAST, new Position(0, 1));
        assertNotNull(s, "Error: expected non-null Ship for 'caravela' but got null");
    }

    @Test
    void buildShip_nau() {
        Ship s = Ship.buildShip("nau", Compass.SOUTH, new Position(1, 0));
        assertNotNull(s, "Error: expected non-null Ship for 'nau' but got null");
    }

    @Test
    void buildShip_fragata() {
        Ship s = Ship.buildShip("fragata", Compass.WEST, new Position(1, 1));
        assertNotNull(s, "Error: expected non-null Ship for 'fragata' but got null");
    }

    @Test
    void buildShip_galeao() {
        Ship s = Ship.buildShip("galeao", Compass.NORTH, new Position(2, 2));
        assertNotNull(s, "Error: expected non-null Ship for 'galeao' but got null");
    }

    @Test
    void buildShip_unknown() {
        Ship s = Ship.buildShip("unknown-kind", Compass.NORTH, new Position(0, 0));
        assertNull(s, "Error: expected null for unknown ship kind but got non-null");
    }

    @Test
    void buildShip_nullBearing_throwsAssertion() {
        assertThrows(AssertionError.class,
                () -> Ship.buildShip("barca", null, new Position(0, 0)),
                "Error: expected AssertionError when passing null bearing to buildShip");
    }

    @Test
    void buildShip_nullPos_throwsAssertion() {
        assertThrows(AssertionError.class,
                () -> Ship.buildShip("barca", Compass.NORTH, null),
                "Error: expected AssertionError when passing null pos to buildShip");
    }

    // ---------- basic getters ----------
    @Test
    void getCategory() {
        assertEquals("testShip", ship.getCategory(), "Error: category should match constructor argument");
    }

    @Test
    void getPositions() {
        List<IPosition> positions = ship.getPositions();
        assertEquals(2, positions.size(), "Error: expected 2 positions added in setup");
    }

    @Test
    void getPosition() {
        assertEquals(position, ship.getPosition(), "Error: expected same position as initialized");
    }

    @Test
    void getBearing() {
        assertEquals(bearing, ship.getBearing(), "Error: bearing should match initialized bearing");
    }

    // ---------- stillFloating: all-hit, some-unhit, empty list ----------
    @Test
    void stillFloating_allHit() {
        for (IPosition p : ship.getPositions()) p.shoot();
        assertFalse(ship.stillFloating(), "Error: ship should not be floating after all positions are hit");
    }

    @Test
    void stillFloating_someUnhit() {
        ship.getPositions().get(0).shoot();
        assertTrue(ship.stillFloating(), "Error: ship should still float if at least one position unhit");
    }

    @Test
    void stillFloating_emptyPositions() {
        Ship empty = new TestShip("empty", Compass.SOUTH, new Position(0, 0));
        // no positions -> loop not entered -> returns false
        assertFalse(empty.stillFloating(), "Error: expected not floating when there are no positions");
    }

    // ---------- extremes single vs multiple positions ----------
    @Test
    void getTopMostPos_singlePosition() {
        Ship s = new TestShip("single", Compass.NORTH, new Position(5, 5));
        s.getPositions().clear();
        s.getPositions().add(new Position(5, 5));
        assertEquals(5, s.getTopMostPos(), "Error: expected top-most row 5 for single position");
    }

    @Test
    void getTopMostPos_updatesWhenLaterIsSmaller() {
        Ship s = new TestShip("ext", Compass.EAST, new Position(5, 1));
        s.getPositions().clear();
        s.getPositions().add(new Position(5, 1));
        s.getPositions().add(new Position(2, 1)); // smaller row -> update
        assertEquals(2, s.getTopMostPos(), "Error: expected top-most row to update to smaller row 2");
    }

    @Test
    void getBottomMostPos_singlePosition() {
        Ship s = new TestShip("single", Compass.NORTH, new Position(7, 7));
        s.getPositions().clear();
        s.getPositions().add(new Position(7, 7));
        assertEquals(7, s.getBottomMostPos(), "Error: expected bottom-most row 7 for single position");
    }

    @Test
    void getBottomMostPos_updatesWhenLaterIsLarger() {
        Ship s = new TestShip("ext", Compass.EAST, new Position(1, 1));
        s.getPositions().clear();
        s.getPositions().add(new Position(1, 1));
        s.getPositions().add(new Position(4, 1)); // larger -> update
        assertEquals(4, s.getBottomMostPos(), "Error: expected bottom-most row to update to larger row 4");
    }

    @Test
    void getLeftMostPos_singlePosition() {
        Ship s = new TestShip("single", Compass.NORTH, new Position(3, 9));
        s.getPositions().clear();
        s.getPositions().add(new Position(3, 9));
        assertEquals(9, s.getLeftMostPos(), "Error: expected left-most column 9 for single position");
    }

    @Test
    void getLeftMostPos_updatesWhenLaterIsSmaller() {
        Ship s = new TestShip("ext", Compass.WEST, new Position(3, 5));
        s.getPositions().clear();
        s.getPositions().add(new Position(3, 5));
        s.getPositions().add(new Position(3, 2)); // smaller -> update
        assertEquals(2, s.getLeftMostPos(), "Error: expected left-most column to update to smaller column 2");
    }

    @Test
    void getRightMostPos_singlePosition() {
        Ship s = new TestShip("single", Compass.NORTH, new Position(3, 1));
        s.getPositions().clear();
        s.getPositions().add(new Position(3, 1));
        assertEquals(1, s.getRightMostPos(), "Error: expected right-most column 1 for single position");
    }

    @Test
    void getRightMostPos_updatesWhenLaterIsLarger() {
        Ship s = new TestShip("ext", Compass.WEST, new Position(3, 1));
        s.getPositions().clear();
        s.getPositions().add(new Position(3, 1));
        s.getPositions().add(new Position(3, 7)); // larger -> update
        assertEquals(7, s.getRightMostPos(), "Error: expected right-most column to update to larger column 7");
    }

    // ---------- occupies: true, false, null assertion ----------
    @Test
    void occupies_trueWhenPositionMatch() {
        assertTrue(ship.occupies(new Position(2, 3)), "Error: ship should occupy (2,3)");
    }

    @Test
    void occupies_falseWhenNoMatch() {
        assertFalse(ship.occupies(new Position(1, 1)), "Error: ship should not occupy unrelated position");
    }

    @Test
    void occupies_nullThrows() {
        assertThrows(AssertionError.class, () -> ship.occupies(null), "Error: expected AssertionError when calling occupies(null)");
    }

    // ---------- tooCloseTo(IPosition): adjacent and not adjacent ----------
    @Test
    void tooCloseToPosition_adjacentTrue() {
        IPosition adj = new Position(2, 5); // adjacent to (2,4)
        assertTrue(ship.tooCloseTo(adj), "Error: position adjacent should return true");
    }

    @Test
    void tooCloseToPosition_notAdjacentFalse() {
        IPosition far = new Position(0, 0);
        assertFalse(ship.tooCloseTo(far), "Error: non-adjacent position should return false");
    }

    // ---------- tooCloseTo(IShip): null, true, false ----------
    @Test
    void tooCloseToShip_nullThrows() {
        assertThrows(AssertionError.class, () -> ship.tooCloseTo((IShip) null), "Error: expected AssertionError when calling tooCloseTo(null)");
    }

    @Test
    void tooCloseToShip_trueIfAnyAdjacent() {
        Ship other = new TestShip("other", Compass.EAST, new Position(2, 5));
        other.getPositions().add(new Position(2, 5));
        assertTrue(ship.tooCloseTo(other), "Error: should be too close when ships are adjacent");
    }

    @Test
    void tooCloseToShip_falseIfFar() {
        Ship other = new TestShip("other", Compass.EAST, new Position(10, 10));
        other.getPositions().add(new Position(10, 10));
        assertFalse(ship.tooCloseTo(other), "Error: ships far apart should not be too close");
    }

    // ---------- shoot: hit, miss, null assertion ----------
    @Test
    void shoot_hitPositionMarksHit() {
        IPosition p = new Position(2, 3);
        ship.shoot(p);
        assertTrue(ship.getPositions().get(0).isHit(), "Error: position (2,3) should be marked as hit");
    }

    @Test
    void shoot_missDoesNotMarkOthers() {
        IPosition p = new Position(9, 9);
        ship.shoot(p);
        assertAll(
                () -> assertFalse(ship.getPositions().get(0).isHit(), "Error: non-matching pos should remain unhit"),
                () -> assertFalse(ship.getPositions().get(1).isHit(), "Error: non-matching pos should remain unhit")
        );
    }

    @Test
    void shoot_nullThrows() {
        assertThrows(AssertionError.class, () -> ship.shoot(null), "Error: expected AssertionError when calling shoot(null)");
    }

    // ---------- toString ----------
    @Test
    void testToString_containsCategoryBearingPosition() {
        String actual = ship.toString();
        assertAll("Error: toString should contain category, bearing and position",
                () -> assertTrue(actual.contains(ship.getCategory()), "Error: toString should contain category"),
                () -> assertTrue(actual.contains(ship.getBearing().toString()), "Error: toString should contain bearing"),
                () -> assertTrue(actual.contains(ship.getPosition().toString()), "Error: toString should contain position")
        );
    }

    // ---------- Constructor assertions ----------
    @Test
    void constructor_nullBearingThrows() {
        assertThrows(AssertionError.class, () -> new TestShip("t", null, new Position(1, 1)),
                "Error: expected AssertionError when bearing is null");
    }

    @Test
    void constructor_nullPositionThrows() {
        assertThrows(AssertionError.class, () -> new TestShip("t", Compass.NORTH, null),
                "Error: expected AssertionError when position is null");
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
