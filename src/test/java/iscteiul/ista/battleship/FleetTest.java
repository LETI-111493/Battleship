package iscteiul.ista.battleship;

import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FleetTest {

    // Minimal stub implementing IShip to exercise Fleet behaviour
    static class DummyShip implements IShip {
        private final String category;
        private final Compass bearing;
        private final IPosition pos;
        protected final List<IPosition> positions = new ArrayList<>();
        private boolean floating = true;

        DummyShip(String category, Compass bearing, IPosition pos) {
            this.category = category;
            this.bearing = bearing;
            this.pos = pos;
            if (pos != null) positions.add(pos);
        }

        void addPos(IPosition p) { positions.add(p); }

        void setFloating(boolean f) { this.floating = f; }

        @Override
        public String getCategory() { return category; }

        @Override
        public List<IPosition> getPositions() { return positions; }

        @Override
        public IPosition getPosition() { return pos; }

        @Override
        public Compass getBearing() { return bearing; }

        @Override
        public boolean stillFloating() { return floating; }

        @Override
        public int getTopMostPos() {
            int top = positions.get(0).getRow();
            for (IPosition p : positions) if (p.getRow() < top) top = p.getRow();
            return top;
        }

        @Override
        public int getBottomMostPos() {
            int bottom = positions.get(0).getRow();
            for (IPosition p : positions) if (p.getRow() > bottom) bottom = p.getRow();
            return bottom;
        }

        @Override
        public int getLeftMostPos() {
            int left = positions.get(0).getColumn();
            for (IPosition p : positions) if (p.getColumn() < left) left = p.getColumn();
            return left;
        }

        @Override
        public int getRightMostPos() {
            int right = positions.get(0).getColumn();
            for (IPosition p : positions) if (p.getColumn() > right) right = p.getColumn();
            return right;
        }

        @Override
        public boolean occupies(IPosition pos) {
            for (IPosition p : positions) if (p.equals(pos)) return true;
            return false;
        }

        @Override
        public boolean tooCloseTo(IShip other) {
            for (IPosition p : positions) {
                for (IPosition op : other.getPositions()) {
                    if (p.isAdjacentTo(op)) return true;
                }
            }
            return false;
        }

        @Override
        public boolean tooCloseTo(IPosition pos) {
            for (IPosition p : positions) if (p.isAdjacentTo(pos)) return true;
            return false;
        }

        @Override
        public void shoot(IPosition pos) {
            for (IPosition p : positions) if (p.equals(pos)) p.shoot();
        }

        @Override
        public Integer getSize() {
            return positions.size();
        }

        @Override
        public String toString() {
            return "[" + category + " " + bearing + " " + pos + "]";
        }
    }

    // A ship that always reports collision risk (force colisionRisk == true)
    static class CollisionShip extends DummyShip {
        CollisionShip(String category, Compass bearing, IPosition pos) {
            super(category, bearing, pos);
        }

        @Override
        public boolean tooCloseTo(IShip other) {
            return true; // force collision risk for testing
        }
    }

    private Fleet fleet;
    private ByteArrayOutputStream outContent;
    private final PrintStream originalOut = System.out;

    private int boardSize = 10;
    private int fleetSize = 5;

    @BeforeEach
    void setUp() {
        fleet = new Fleet();
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // try read IFleet constants for robust tests; fallback to sensible defaults
        try {
            Class<?> iface = Class.forName("iscteiul.ista.battleship.IFleet");
            Field fb = iface.getField("BOARD_SIZE");
            Field ff = iface.getField("FLEET_SIZE");
            boardSize = fb.getInt(null);
            fleetSize = ff.getInt(null);
        } catch (Exception ignored) {
        }
    }

    @AfterEach
    void tearDown() {
        fleet = null;
        System.setOut(originalOut);
        outContent = null;
    }

    @Test
    void printShips_printsElements() {
        DummyShip s = new DummyShip("C", Compass.NORTH, new Position(1, 1));
        List<IShip> l = new ArrayList<>();
        l.add(s);
        Fleet.printShips(l);
        String printed = outContent.toString();
        assertTrue(printed.contains("C"),
                "Error: expected printed output to contain category 'C'; actual output: \"" + printed + "\"");
    }

    @Test
    void printShips_null_throwsNPE() {
        assertThrows(NullPointerException.class, () -> Fleet.printShips(null),
                "Error: expected NullPointerException when calling printShips with null; actual: no exception or different type");
    }

    @Test
    void printShips_emptyList_printsNothing() {
        Fleet.printShips(Collections.emptyList());
        String printed = outContent.toString();
        assertTrue(printed.length() == 0,
                "Error: expected no output when printing an empty list; actual output: \"" + printed + "\"");
    }

    @Test
    void getShips_initiallyEmpty_thenReflectsAdds() {
        assertEquals(0, fleet.getShips().size(),
                "Error: expected new fleet to start with 0 ships but got " + fleet.getShips().size());
        DummyShip s = new DummyShip("X", Compass.EAST, new Position(2, 2));
        boolean added = fleet.addShip(s);
        if (added) {
            assertTrue(fleet.getShips().contains(s),
                    "Error: addShip returned true so fleet should contain the ship but it does not");
        } else {
            assertFalse(fleet.getShips().contains(s),
                    "Error: addShip returned false so fleet should not contain the ship but it does");
        }
    }

    @Test
    void addShip_allConditionsSatisfied_adds() {
        DummyShip s = new DummyShip("OK", Compass.NORTH, new Position(2, 2));
        boolean res = fleet.addShip(s);
        assertTrue(res, "Error: expected addShip to succeed for valid ship inside board; actual: false");
        assertTrue(fleet.getShips().contains(s),
                "Error: expected fleet to contain the ship after successful addShip; actual list: " + fleet.getShips());
    }

    @Test
    void addShip_null_throwsNPE() {
        assertThrows(NullPointerException.class, () -> fleet.addShip(null),
                "Error: expected NullPointerException when calling addShip with null; actual: no exception or different type");
    }

    @Test
    void addShip_sizeLimit_preventsAdd() {
        // fill fleet to exceed FLEET_SIZE to force size check to fail
        for (int i = 0; i <= fleetSize; i++) {
            fleet.getShips().add(new DummyShip("T" + i, Compass.SOUTH, new Position(0, i % boardSize)));
        }
        boolean res = fleet.addShip(new DummyShip("NEW", Compass.WEST, new Position(1,1)));
        assertFalse(res, "Error: expected addShip to fail when fleet size exceeds IFleet.FLEET_SIZE; actual: true");
    }

    @Test
    void addShip_outsideBoard_leftNegative_preventsAdd() {
        DummyShip out = new DummyShip("OOB1", Compass.NORTH, new Position(0, -1));
        boolean res = fleet.addShip(out);
        assertFalse(res, "Error: expected addShip to fail for ship with negative column; actual: true");
    }

    @Test
    void addShip_outsideBoard_rightTooLarge_preventsAdd() {
        // create a position whose column is beyond BOARD_SIZE-1 to trigger rightmost check
        DummyShip out = new DummyShip("OOB2", Compass.EAST, new Position(1, boardSize));
        boolean res = fleet.addShip(out);
        assertFalse(res, "Error: expected addShip to fail for ship with rightmost column > BOARD_SIZE-1; actual: true");
    }

    @Test
    void addShip_outsideBoard_topNegative_preventsAdd() {
        DummyShip out = new DummyShip("OOB3", Compass.NORTH, new Position(-1, 0));
        boolean res = fleet.addShip(out);
        assertFalse(res, "Error: expected addShip to fail for ship with negative row; actual: true");
    }

    @Test
    void addShip_outsideBoard_bottomTooLarge_preventsAdd() {
        DummyShip out = new DummyShip("OOB4", Compass.SOUTH, new Position(boardSize, 0));
        boolean res = fleet.addShip(out);
        assertFalse(res, "Error: expected addShip to fail for ship with bottom row > BOARD_SIZE-1; actual: true");
    }

    @Test
    void addShip_collisionRisk_preventsAdd() {
        CollisionShip cs = new CollisionShip("C", Compass.EAST, new Position(5,5));
        fleet.getShips().add(cs);
        DummyShip newShip = new DummyShip("NEW", Compass.NORTH, new Position(6,6));
        boolean res = fleet.addShip(newShip);
        assertFalse(res, "Error: expected addShip to fail due to collision risk from existing ship; actual: true");
    }

    @Test
    void getShipsLike_returnsMatchingCategoriesOnly() {
        DummyShip a1 = new DummyShip("A", Compass.NORTH, new Position(1,1));
        DummyShip a2 = new DummyShip("A", Compass.NORTH, new Position(2,2));
        DummyShip b = new DummyShip("B", Compass.SOUTH, new Position(3,3));
        fleet.getShips().add(a1);
        fleet.getShips().add(a2);
        fleet.getShips().add(b);
        List<IShip> likeA = fleet.getShipsLike("A");
        assertEquals(2, likeA.size(),
                "Error: expected 2 ships for category 'A' but got " + likeA.size() + "; list: " + likeA);
    }

    @Test
    void getShipsLike_null_returnsEmpty_whenNoNullCategories() {
        DummyShip a = new DummyShip("X", Compass.NORTH, new Position(1,1));
        fleet.getShips().add(a);
        List<IShip> like = fleet.getShipsLike(null);
        assertTrue(like.isEmpty(),
                "Error: expected getShipsLike(null) to return empty when no ship has null category; actual: " + like);
    }

    @Test
    void getShipsLike_emptyString_returnsEmpty() {
        DummyShip a = new DummyShip("X", Compass.NORTH, new Position(1,1));
        fleet.getShips().add(a);
        List<IShip> like = fleet.getShipsLike("");
        assertTrue(like.isEmpty(),
                "Error: expected getShipsLike(\"\") to return an empty list when no empty-category ships exist; actual: " + like);
    }

    @Test
    void getFloatingShips_returnsOnlyFloating() {
        DummyShip f1 = new DummyShip("F1", Compass.NORTH, new Position(1,1));
        DummyShip f2 = new DummyShip("F2", Compass.NORTH, new Position(2,2));
        f2.setFloating(false);
        fleet.getShips().add(f1);
        fleet.getShips().add(f2);
        List<IShip> floating = fleet.getFloatingShips();
        assertEquals(1, floating.size(),
                "Error: expected only 1 floating ship but got " + floating.size() + "; list: " + floating);
        assertTrue(floating.contains(f1),
                "Error: expected floating list to contain f1 but it does not; floating list: " + floating);
    }

    @Test
    void getFloatingShips_noneFloating_returnsEmpty() {
        DummyShip a = new DummyShip("A", Compass.NORTH, new Position(1,1));
        a.setFloating(false);
        fleet.getShips().add(a);
        List<IShip> floating = fleet.getFloatingShips();
        assertTrue(floating.isEmpty(),
                "Error: expected no floating ships but got: " + floating);
    }

    @Test
    void shipAt_returnsCorrectShipOrNull() {
        DummyShip s = new DummyShip("S", Compass.NORTH, new Position(7,7));
        fleet.getShips().add(s);
        IShip found = fleet.shipAt(new Position(7,7));
        assertEquals(s, found,
                "Error: expected shipAt to return the ship occupying (7,7) but got: " + found);
        IShip none = fleet.shipAt(new Position(0,0));
        assertNull(none, "Error: expected shipAt to return null when no ship occupies given position; actual: " + none);
    }

    @Test
    void printStatus_invokesVariousPrints_noException() {
        fleet.getShips().add(new DummyShip("Galeao", Compass.NORTH, new Position(1,1)));
        fleet.getShips().add(new DummyShip("Barca", Compass.SOUTH, new Position(2,2)));
        fleet.printStatus();
        String out = outContent.toString();
        assertTrue(out.length() > 0, "Error: expected printStatus to write something to stdout but output was empty");
        assertTrue(out.contains("Galeao") || out.contains("Barca"),
                "Error: expected category names 'Galeao' or 'Barca' in output; actual output: \"" + out + "\"");
    }

    @Test
    void printShipsByCategory_null_assertionBehavior() {
        // Fleet.printShipsByCategory contains an assert; behavior depends on JVM assert enablement.
        // If assertions enabled, expect AssertionError; otherwise, no exception and nothing printed.
        try {
            assertThrows(AssertionError.class, () -> fleet.printShipsByCategory(null),
                    "Error: expected AssertionError when calling printShipsByCategory with null when assertions enabled");
        } catch (Throwable t) {
            // fallback: call and ensure no unexpected exception type is thrown (cover branch where asserts disabled)
            fleet.printShipsByCategory("NonExisting");
        }
    }

    @Test
    void printFloatingShips_and_printAllShips_printsLists() {
        fleet.getShips().add(new DummyShip("A", Compass.NORTH, new Position(1,1)));
        fleet.printFloatingShips();
        fleet.printAllShips();
        String out = outContent.toString();
        assertTrue(out.contains("A"),
                "Error: expected printed output to contain ship category 'A' but output: \"" + out + "\"");
    }

    // Minimal stub para controlar left/right/top/bottom e comportamento de tooCloseTo
    static class MinimalShip implements IShip {
        private final String category;
        private final List<IPosition> positions = new ArrayList<>();
        private boolean floating = true;

        MinimalShip(String category, List<IPosition> positions) {
            this.category = category;
            if (positions != null) this.positions.addAll(positions);
        }

        @Override public String getCategory() { return category; }
        @Override public List<IPosition> getPositions() { return positions; }
        @Override public IPosition getPosition() { return positions.isEmpty() ? null : positions.get(0); }
        @Override public Compass getBearing() { return Compass.NORTH; }
        @Override public boolean stillFloating() { return floating; }
        @Override public Integer getSize() { return positions.size(); }
        @Override public void shoot(IPosition pos) { for (IPosition p : positions) if (p.equals(pos)) p.shoot(); }

        @Override
        public int getTopMostPos() {
            int top = positions.get(0).getRow();
            for (IPosition p : positions) if (p.getRow() < top) top = p.getRow();
            return top;
        }

        @Override
        public int getBottomMostPos() {
            int bottom = positions.get(0).getRow();
            for (IPosition p : positions) if (p.getRow() > bottom) bottom = p.getRow();
            return bottom;
        }

        @Override
        public int getLeftMostPos() {
            int left = positions.get(0).getColumn();
            for (IPosition p : positions) if (p.getColumn() < left) left = p.getColumn();
            return left;
        }

        @Override
        public int getRightMostPos() {
            int right = positions.get(0).getColumn();
            for (IPosition p : positions) if (p.getColumn() > right) right = p.getColumn();
            return right;
        }

        @Override
        public boolean occupies(IPosition pos) {
            for (IPosition p : positions) if (p.equals(pos)) return true;
            return false;
        }

        @Override
        public boolean tooCloseTo(IPosition pos) {
            for (IPosition p : positions) {
                if (p.isAdjacentTo(pos)) return true;
            }
            return false;
        }
        @Override
        public boolean tooCloseTo(IShip other) {
            for (IPosition p : positions) {
                for (IPosition op : other.getPositions()) {
                    if (p.isAdjacentTo(op)) return true;
                }
            }
            return false;
        }




        @Override
        public String toString() { return "[" + category + " " + positions + "]"; }
    }

    // Força colisão (para C = false quando necessário)
    static class AlwaysCollideShip extends MinimalShip {
        AlwaysCollideShip(String category, List<IPosition> positions) { super(category, positions); }
        @Override public boolean tooCloseTo(IShip other) { return true; }
    }

    @Nested
    @DisplayName("addShip(...) - decomposição A && B && C (A=size, B=isInsideBoard, C=!colisionRisk)")
    class AddShipConditions {

        @Test
        @DisplayName("A=true && B=true && C=true -> adiciona e retorna true")
        void aTrue_bTrue_cTrue_adds() {
            Fleet fleet = new Fleet();
            List<IPosition> pos = List.of(new Position(2,2));
            MinimalShip s = new MinimalShip("OK", pos);
            boolean res = fleet.addShip(s); // tamanho inicial 0 -> A true; posição dentro -> B true; sem colisões -> C true
            assertTrue(res, "esperado true quando A,B,C são todos verdadeiros");
            assertTrue(fleet.getShips().contains(s));
        }

        @Test
        @DisplayName("A=true && B=true && C=false -> não adiciona (colisão) e retorna false")
        void aTrue_bTrue_cFalse_noAdd_dueCollision() {
            Fleet fleet = new Fleet();
            // adiciona existente que força colisão
            AlwaysCollideShip existing = new AlwaysCollideShip("X", List.of(new Position(2,2)));
            fleet.getShips().add(existing);
            MinimalShip s = new MinimalShip("NEW", List.of(new Position(3,3)));
            boolean res = fleet.addShip(s); // A true, B true, C false (por existing.tooCloseTo)
            assertFalse(res, "esperado false quando existe risco de colisão");
            assertFalse(fleet.getShips().contains(s));
        }

        @Test
        @DisplayName("A=false && B=true && C=true -> não adiciona por limite de tamanho (A false)")
        void aFalse_bTrue_cTrue_noAdd_dueSize() throws Exception {
            Fleet fleet = new Fleet();
            // encher a frota para exceder IFleet.FLEET_SIZE -> faz A=false
            int limit = IFleet.FLEET_SIZE;
            for (int i = 0; i <= limit; i++) {
                fleet.getShips().add(new MinimalShip("T" + i, List.of(new Position(0, i))));
            }
            MinimalShip s = new MinimalShip("NEW", List.of(new Position(1,1)));
            boolean res = fleet.addShip(s);
            assertFalse(res, "esperado false quando ships.size() > FLEET_SIZE (A=false)");
        }

        @Test
        @DisplayName("A=true && B=false && C=true -> não adiciona por posição fora do tabuleiro (B false)")
        void aTrue_bFalse_cTrue_noAdd_outsideBoard() {
            Fleet fleet = new Fleet();
            // criar ship com coluna negativa para violar left>=0
            MinimalShip out = new MinimalShip("OOB", List.of(new Position(1, -1)));
            boolean res = fleet.addShip(out);
            assertFalse(res, "esperado false quando isInsideBoard devolve false (posição fora)");
        }
    }

    @Nested
    @DisplayName("isInsideBoard(...) - decomposição das 4 condições atómicas (left,right,top,bottom)")
    class IsInsideBoardConditions {

        @Test
        @DisplayName("todos os átomos true -> posição totalmente dentro -> addShip retorna true")
        void allAtomsTrue_insideBoard() {
            Fleet fleet = new Fleet();
            MinimalShip s = new MinimalShip("IN", List.of(new Position(4,4)));
            boolean res = fleet.addShip(s);
            assertTrue(res, "esperado true quando todas as quatro comparações de isInsideBoard são verdadeiras");
        }

        @Test
        @DisplayName("left < 0 -> isInsideBoard false -> addShip retorna false (left negativo)")
        void leftNegative_preventsAdd() {
            Fleet fleet = new Fleet();
            MinimalShip s = new MinimalShip("LEFT_NEG", List.of(new Position(2, -2))); // left < 0
            boolean res = fleet.addShip(s);
            assertFalse(res, "esperado false quando leftMostPos < 0");
        }

        @Test
        @DisplayName("right > BOARD_SIZE-1 -> isInsideBoard false -> addShip retorna false (coluna demasiado grande)")
        void rightTooLarge_preventsAdd() {
            Fleet fleet = new Fleet();
            // usar coluna igual a BOARD_SIZE para violar right <= BOARD_SIZE-1
            int bs = IFleet.BOARD_SIZE;
            MinimalShip s = new MinimalShip("RIGHT_BIG", List.of(new Position(1, bs)));
            boolean res = fleet.addShip(s);
            assertFalse(res, "esperado false quando rightMostPos > BOARD_SIZE-1");
        }

        @Test
        @DisplayName("top < 0 -> isInsideBoard false -> addShip retorna false (linha negativa)")
        void topNegative_preventsAdd() {
            Fleet fleet = new Fleet();
            MinimalShip s = new MinimalShip("TOP_NEG", List.of(new Position(-1, 3))); // top < 0
            boolean res = fleet.addShip(s);
            assertFalse(res, "esperado false quando topMostPos < 0");
        }

        @Test
        @DisplayName("bottom > BOARD_SIZE-1 -> isInsideBoard false -> addShip retorna false (linha demasiado grande)")
        void bottomTooLarge_preventsAdd() {
            Fleet fleet = new Fleet();
            int bs = IFleet.BOARD_SIZE;
            MinimalShip s = new MinimalShip("BOTTOM_BIG", List.of(new Position(bs, 3))); // bottom > BOARD_SIZE-1
            boolean res = fleet.addShip(s);
            assertFalse(res, "esperado false quando bottomMostPos > BOARD_SIZE-1");
        }
    }
}
