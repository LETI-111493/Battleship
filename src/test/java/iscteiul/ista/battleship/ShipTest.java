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

    @Nested
    @DisplayName("Fábrica buildShip - todos os ramos do switch")
    class BuildTests {
        @Test
        @DisplayName("buildShip devolve instâncias para kinds conhecidos e null para unknown")
        void testBuildShipAllKindsAndUnknown() {
            String[] kinds = {"barca", "caravela", "nau", "fragata", "galeao"};
            for (String k : kinds) {
                assertNotNull(Ship.buildShip(k, Compass.NORTH, new Position(0, 0)),
                        "Esperado instância não-nula para kind=" + k);
            }
            assertNull(Ship.buildShip("inexistente", Compass.NORTH, new Position(0, 0)),
                    "Esperado null para kind desconhecido");
        }
    }

    // ---------- Estado e representação ----------
    @Nested
    @DisplayName("Getters e toString")
    class StateTests {
        @Test
        @DisplayName("getCategory/getBearing/getPosition/getPositions e toString contêm os componentes")
        void testGettersAndToString() {
            TestShip s = new TestShip("minha", Compass.EAST, new Position(4, 4));
            s.getPositions().add(new Position(4, 4));
            List<IPosition> pos = s.getPositions();
            assertAll(
                    () -> assertEquals("minha", s.getCategory()),
                    () -> assertEquals(Compass.EAST, s.getBearing()),
                    () -> assertEquals(new Position(4, 4), s.getPosition()),
                    () -> assertEquals(1, pos.size()),
                    () -> {
                        String t = s.toString();
                        assertTrue(t.contains("minha"));
                        assertTrue(t.contains(Compass.EAST.toString()));
                        assertTrue(t.contains(s.getPosition().toString()));
                    }
            );
        }
    }

    // ---------- Extremos (top/bottom/left/right) com ramos update e single ----------
    @Nested
    @DisplayName("Cálculo de extremos - caminhos single e update")
    class ExtremesTests {
        @Test
        @DisplayName("getTopMost/getBottomMost/getLeftMost/getRightMost: single e atualização de valor")
        void testExtremesSingleAndUpdate() {
            TestShip s = new TestShip("ext", Compass.NORTH, new Position(0, 0));
            s.getPositions().clear();
            s.getPositions().add(new Position(5, 5)); // single -> extremes = 5,5
            assertAll(
                    () -> assertEquals(5, s.getTopMostPos()),
                    () -> assertEquals(5, s.getBottomMostPos()),
                    () -> assertEquals(5, s.getLeftMostPos()),
                    () -> assertEquals(5, s.getRightMostPos())
            );
            // adiciona posições que forçam updates (menor/maior)
            s.getPositions().add(new Position(2, 8)); // top menor, right maior, left unchanged
            s.getPositions().add(new Position(9, 3)); // bottom maior, left menor
            assertAll(
                    () -> assertEquals(2, s.getTopMostPos()),
                    () -> assertEquals(9, s.getBottomMostPos()),
                    () -> assertEquals(3, s.getLeftMostPos()),
                    () -> assertEquals(8, s.getRightMostPos())
            );
        }
    }

    // ---------- Ocupação e tiro (occupies, shoot) incluindo asserts de null ----------
    @Nested
    @DisplayName("occupies e shoot - caminhos true/false e assertions")
    class InteractionTests {
        @Test
        @DisplayName("occupies verdadeiro/falso, occupies(null) lança, shoot marca e shoot(null) lança")
        void testOccupiesAndShoot() {
            TestShip s = new TestShip("int", Compass.NORTH, new Position(2, 3));
            s.getPositions().clear();
            s.getPositions().add(new Position(2, 3));
            s.getPositions().add(new Position(2, 4));
            // occupies true/false
            assertTrue(s.occupies(new Position(2, 3)));
            assertFalse(s.occupies(new Position(0, 0)));
            // occupies(null) lança AssertionError
            assertThrows(AssertionError.class, () -> s.occupies(null));
            // shoot acerta e marca hit
            s.shoot(new Position(2, 3));
            assertTrue(s.getPositions().get(0).isHit());
            // shoot que erra não marca os outros
            s.getPositions().get(1).shoot(); // garante estado conhecido
            s.shoot(new Position(9, 9));
            assertTrue(s.getPositions().get(1).isHit(), "posição 2 deveria manter seu estado marcado explicitamente");
            // shoot(null) lança AssertionError
            assertThrows(AssertionError.class, () -> s.shoot(null));
        }
    }

    // ---------- Proximidade (tooCloseTo) para posição e navio ----------
    @Nested
    @DisplayName("tooCloseTo - posição e navio (null / true / false)")
    class ProximityTests {
        @Test
        @DisplayName("tooCloseTo(IPosition) verdadeiro/falso e tooCloseTo(IShip) null/true/false")
        void testTooCloseToPositionAndShip() {
            TestShip s = new TestShip("prox", Compass.NORTH, new Position(2, 2));
            s.getPositions().clear();
            s.getPositions().add(new Position(2, 2));
            s.getPositions().add(new Position(2, 3));
            // posição adjacente -> true
            assertTrue(s.tooCloseTo(new Position(2, 4)));
            // posição distante -> false
            assertFalse(s.tooCloseTo(new Position(0, 0)));
            // tooCloseTo(IShip) null lança
            assertThrows(AssertionError.class, () -> s.tooCloseTo((IShip) null));
            // outra ship adjacente -> true
            TestShip other = new TestShip("o", Compass.EAST, new Position(2, 4));
            other.getPositions().add(new Position(2, 4));
            assertTrue(s.tooCloseTo(other));
            // outra ship distante -> false
            TestShip far = new TestShip("f", Compass.SOUTH, new Position(10, 10));
            far.getPositions().add(new Position(10, 10));
            assertFalse(s.tooCloseTo(far));
        }
    }

    // ---------- stillFloating e construtor assertions ----------
    @Nested
    @DisplayName("stillFloating e construtor - condições de fronteira e assertions")
    class FloatingAndConstructorTests {
        @Test
        @DisplayName("stillFloating: todos atingidos / algum não atingido / lista vazia e construtor com null lança")
        void testStillFloatingAndConstructorAssertions() {
            // todos atingidos -> false
            TestShip sAll = new TestShip("f", Compass.NORTH, new Position(1, 1));
            sAll.getPositions().add(new Position(1, 1));
            sAll.getPositions().get(0).shoot();
            assertFalse(sAll.stillFloating());
            // algum não atingido -> true
            TestShip sSome = new TestShip("g", Compass.NORTH, new Position(2, 2));
            sSome.getPositions().add(new Position(2, 2));
            sSome.getPositions().add(new Position(2, 3));
            sSome.getPositions().get(0).shoot();
            assertTrue(sSome.stillFloating());
            // lista vazia -> loop não entra -> false
            TestShip empty = new TestShip("e", Compass.SOUTH, new Position(0, 0));
            assertFalse(empty.stillFloating());
            // construtor com bearing null e pos null lança AssertionError quando assertions ativas
            assertThrows(AssertionError.class, () -> new TestShip("x", null, new Position(0, 0)));
            assertThrows(AssertionError.class, () -> new TestShip("x", Compass.NORTH, null));
        }
    }
}
