package iscteiul.ista.battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Revised unit tests for {@link Ship} aiming at maximum branch/line coverage,
 * organized using @Nested as per task D.3.
 */
class ShipTest {

    // --- Subclasse de apoio ---
    static class TestShip extends Ship {
        public TestShip(String category, Compass bearing, IPosition pos) {
            super(category, bearing, pos);
        }

        @Override
        public Integer getSize() {
            // Returns the actual number of positions for this test Ship
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
        // The TestShip uses the base Ship constructor logic
        ship = new TestShip("testShip", bearing, position);
        // Add two positions for testing floating and bounds logic
        ship.getPositions().add(new Position(2, 3));
        ship.getPositions().add(new Position(2, 4));
    }

    @AfterEach
    void tearDown() {
        ship = null;
        position = null;
        bearing = null;
    }

    //-------------------------------------------------------------

    @Nested
    @DisplayName("A. Ship Factory, Constructor, and Getters Tests (Branch Coverage)")
    class BuildShipAndConstructorTests {

        // Tests covering all cases of the buildShip switch (decision/branch coverage)
        @Test
        void buildShip_barca() {
            Ship s = Ship.buildShip("barca", Compass.NORTH, new Position(0, 0));
            assertNotNull(s, "Error: expected non-null Ship for 'barca'");
        }

        @Test
        void buildShip_caravela() {
            Ship s = Ship.buildShip("caravela", Compass.EAST, new Position(0, 1));
            assertNotNull(s, "Error: expected non-null Ship for 'caravela'");
        }

        @Test
        void buildShip_nau() {
            Ship s = Ship.buildShip("nau", Compass.SOUTH, new Position(1, 0));
            assertNotNull(s, "Error: expected non-null Ship for 'nau'");
        }

        @Test
        void buildShip_fragata() {
            Ship s = Ship.buildShip("fragata", Compass.WEST, new Position(1, 1));
            assertNotNull(s, "Error: expected non-null Ship for 'fragata'");
        }

        @Test
        void buildShip_galeao() {
            Ship s = Ship.buildShip("galeao", Compass.NORTH, new Position(2, 2));
            assertNotNull(s, "Error: expected non-null Ship for 'galeao'");
        }

        // Tests covering the default branch (decision/branch coverage)
        @Test
        void buildShip_unknown() {
            Ship s = Ship.buildShip("unknown-kind", Compass.NORTH, new Position(0, 0));
            assertNull(s, "Error: expected null for unknown ship kind");
        }

        // Tests covering constructor null assertion branches (decision/branch coverage)
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

        // ---------- basic getters (needed for coverage of the methods themselves) ----------
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
    }

    //-------------------------------------------------------------

    @Nested
    @DisplayName("B. Ship Core Logic and Boundary Tests (Branch Coverage)")
    class InstanceMethodTests {

        // ---------- stillFloating: cover all if/loop branches (decision/branch coverage) ----------
        @Test
        void stillFloating_allHit() {
            for (IPosition p : ship.getPositions()) p.shoot();
            assertFalse(ship.stillFloating(), "Error: ship should not be floating after all positions are hit");
        }

        @Test
        void stillFloating_someUnhit() {
            ship.getPositions().get(0).shoot();
            // Loop enters, loop breaks when finding unhit position (true branch)
            assertTrue(ship.stillFloating(), "Error: ship should still float if at least one position unhit");
        }

        @Test
        void stillFloating_emptyPositions() {
            Ship empty = new TestShip("empty", Compass.SOUTH, new Position(0, 0));
            // Loop is skipped (false branch)
            assertFalse(empty.stillFloating(), "Error: expected not floating when there are no positions");
        }

        // ---------- Boundary Getters: covering update (true) and no-update (false) branches ----------

        @Test
        void getTopMostPos_updatesWhenLaterIsSmaller() {
            Ship s = new TestShip("ext", Compass.EAST, new Position(5, 1));
            s.getPositions().clear();
            s.getPositions().add(new Position(5, 1));
            s.getPositions().add(new Position(2, 1)); // 2 < 5 is TRUE (hit true branch)
            assertEquals(2, s.getTopMostPos(), "Error: expected top-most row to update to smaller row 2");
        }

        @Test
        void getTopMostPos_initialValueMaintained() {
            Ship s = new TestShip("ext", Compass.EAST, new Position(2, 1));
            s.getPositions().clear();
            s.getPositions().add(new Position(2, 1)); // topRow = 2
            s.getPositions().add(new Position(5, 1)); // 5 < 2 is FALSE (hit false branch)
            assertEquals(2, s.getTopMostPos(), "Error: expected top-most row 2 to be maintained");
        }

        @Test
        void getBottomMostPos_updatesWhenLaterIsLarger() {
            Ship s = new TestShip("ext", Compass.EAST, new Position(1, 1));
            s.getPositions().clear();
            s.getPositions().add(new Position(1, 1));
            s.getPositions().add(new Position(4, 1)); // 4 > 1 is TRUE (hit true branch)
            assertEquals(4, s.getBottomMostPos(), "Error: expected bottom-most row to update to larger row 4");
        }

        @Test
        void getBottomMostPos_initialValueMaintained() {
            Ship s = new TestShip("ext", Compass.EAST, new Position(5, 1));
            s.getPositions().clear();
            s.getPositions().add(new Position(5, 1)); // bottomRow = 5
            s.getPositions().add(new Position(2, 1)); // 2 > 5 is FALSE (hit false branch)
            assertEquals(5, s.getBottomMostPos(), "Error: expected bottom-most row 5 to be maintained");
        }

        @Test
        void getLeftMostPos_updatesWhenLaterIsSmaller() {
            Ship s = new TestShip("ext", Compass.WEST, new Position(3, 5));
            s.getPositions().clear();
            s.getPositions().add(new Position(3, 5));
            s.getPositions().add(new Position(3, 2)); // 2 < 5 is TRUE (hit true branch)
            assertEquals(2, s.getLeftMostPos(), "Error: expected left-most column to update to smaller column 2");
        }

        @Test
        void getLeftMostPos_initialValueMaintained() {
            Ship s = new TestShip("ext", Compass.WEST, new Position(3, 2));
            s.getPositions().clear();
            s.getPositions().add(new Position(3, 2)); // leftCol = 2
            s.getPositions().add(new Position(3, 5)); // 5 < 2 is FALSE (hit false branch)
            assertEquals(2, s.getLeftMostPos(), "Error: expected left-most column 2 to be maintained");
        }

        @Test
        void getRightMostPos_updatesWhenLaterIsLarger() {
            Ship s = new TestShip("ext", Compass.WEST, new Position(3, 1));
            s.getPositions().clear();
            s.getPositions().add(new Position(3, 1));
            s.getPositions().add(new Position(3, 7)); // 7 > 1 is TRUE (hit true branch)
            assertEquals(7, s.getRightMostPos(), "Error: expected right-most column to update to larger column 7");
        }

        @Test
        void getRightMostPos_initialValueMaintained() {
            Ship s = new TestShip("ext", Compass.WEST, new Position(3, 7));
            s.getPositions().clear();
            s.getPositions().add(new Position(3, 7)); // rightCol = 7
            s.getPositions().add(new Position(3, 1)); // 1 > 7 is FALSE (hit false branch)
            assertEquals(7, s.getRightMostPos(), "Error: expected right-most column 7 to be maintained");
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

        // ---------- tooCloseTo(IPosition): adjacent and not adjacent (branch coverage) ----------
        @Test
        void tooCloseToPosition_adjacentTrue() {
            IPosition adj = new Position(2, 5); // adjacent to (2,4)
            assertTrue(ship.tooCloseTo(adj), "Error: position adjacent should return true (branch true)");
        }

        @Test
        void tooCloseToPosition_notAdjacentFalse() {
            IPosition far = new Position(0, 0);
            assertFalse(ship.tooCloseTo(far), "Error: non-adjacent position should return false (branch false)");
        }

        // ---------- tooCloseTo(IShip): null, true, false (branch coverage) ----------
        @Test
        void tooCloseToShip_nullThrows() {
            assertThrows(AssertionError.class, () -> ship.tooCloseTo((IShip) null), "Error: expected AssertionError when calling tooCloseTo(null)");
        }

        @Test
        void tooCloseToShip_trueIfAnyAdjacent() {
            Ship other = new TestShip("other", Compass.EAST, new Position(2, 5));
            other.getPositions().add(new Position(2, 5));
            // Outer loop breaks early (branch true)
            assertTrue(ship.tooCloseTo(other), "Error: should be too close when ships are adjacent");
        }

        @Test
        void tooCloseToShip_falseIfFar() {
            Ship other = new TestShip("other", Compass.EAST, new Position(10, 10));
            other.getPositions().add(new Position(10, 10));
            // Outer loop completes (branch false)
            assertFalse(ship.tooCloseTo(other), "Error: ships far apart should not be too close");
        }

        // ---------- shoot: hit, miss, null assertion ----------
        @Test
        void shoot_hitPositionMarksHit() {
            IPosition p = new Position(2, 3);
            ship.shoot(p);
            // Internal loop finds match (branch true)
            assertTrue(ship.getPositions().get(0).isHit(), "Error: position (2,3) should be marked as hit");
        }

        @Test
        void shoot_missDoesNotMarkOthers() {
            IPosition p = new Position(9, 9);
            ship.shoot(p);
            // Internal loop does not find match (branch false)
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
    }
}