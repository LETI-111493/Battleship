package iscteiul.ista.battleship;

import org.junit.jupiter.api.*; // Importa tudo, incluindo @Nested e @DisplayName

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests for Fleet Class (Path Coverage Focus)")
class FleetTest {

    // --- CLASSES STUB MANTIDAS (omito o corpo para brevidade, mas devem ser mantidas) ---
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

        @Override public String getCategory() { return category; }
        @Override public List<IPosition> getPositions() { return positions; }
        @Override public IPosition getPosition() { return pos; }
        @Override public Compass getBearing() { return bearing; }
        @Override public boolean stillFloating() { return floating; }

        // --- Implementações simplificadas de Bounds/Occupies/TooCloseTo/Shoot ---
        @Override
        public int getTopMostPos() {
            if (positions.isEmpty()) return 0; // Previne NPE em navios vazios
            int top = positions.get(0).getRow();
            for (IPosition p : positions) if (p.getRow() < top) top = p.getRow();
            return top;
        }

        @Override
        public int getBottomMostPos() {
            if (positions.isEmpty()) return 0;
            int bottom = positions.get(0).getRow();
            for (IPosition p : positions) if (p.getRow() > bottom) bottom = p.getRow();
            return bottom;
        }

        @Override
        public int getLeftMostPos() {
            if (positions.isEmpty()) return 0;
            int left = positions.get(0).getColumn();
            for (IPosition p : positions) if (p.getColumn() < left) left = p.getColumn();
            return left;
        }

        @Override
        public int getRightMostPos() {
            if (positions.isEmpty()) return 0;
            int right = positions.get(0).getColumn();
            for (IPosition p : positions) if (p.getColumn() > right) right = p.getColumn();
            return right;
        }

        @Override
        public boolean occupies(IPosition pos) {
            if (pos == null) throw new AssertionError();
            for (IPosition p : positions) if (p.equals(pos)) return true;
            return false;
        }

        @Override
        public boolean tooCloseTo(IShip other) {
            if (other == null) throw new AssertionError();
            for (IPosition p : positions) {
                for (IPosition op : other.getPositions()) {
                    if (p.isAdjacentTo(op)) return true;
                }
            }
            return false;
        }

        @Override
        public boolean tooCloseTo(IPosition pos) {
            if (pos == null) throw new NullPointerException();
            for (IPosition p : positions) if (p.isAdjacentTo(pos)) return true;
            return false;
        }

        @Override
        public void shoot(IPosition pos) {
            if (pos == null) throw new AssertionError();
            for (IPosition p : positions) if (p.equals(pos)) p.shoot();
        }

        @Override public Integer getSize() { return positions.size(); }
        @Override public String toString() { return "[" + category + " " + bearing + " " + pos + "]"; }
    }

    static class CollisionShip extends DummyShip {
        CollisionShip(String category, Compass bearing, IPosition pos) { super(category, bearing, pos); }
        @Override public boolean tooCloseTo(IShip other) { return true; } // force collision risk for testing
    }
    // --- FIM STUBS ---

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

    // -------------------------------------------------------------

    @Nested
    @DisplayName("A. AddShip Validation Paths (Path Coverage)")
    class AddShipValidationPaths {

        @Test
        @DisplayName("Path 1: Success (Inside board, no collision, size OK)")
        void addShip_allConditionsSatisfied_adds() {
            DummyShip s = new DummyShip("OK", Compass.NORTH, new Position(2, 2));
            boolean res = fleet.addShip(s);
            assertTrue(res, "Expected addShip to succeed for valid ship inside board");
            assertTrue(fleet.getShips().contains(s));
        }

        @Test
        @DisplayName("Path 2: Fail due to Occupied Position (Collision with occupied pos)")
        void addShip_collisionWithOccupiedPos_preventsAdd() {
            // Adiciona um navio para ocupar (5, 5)
            DummyShip existing = new DummyShip("A", Compass.NORTH, new Position(5, 5));
            fleet.getShips().add(existing);

            // Tenta adicionar um navio na mesma posição
            DummyShip newShip = new DummyShip("B", Compass.SOUTH, new Position(5, 5));
            boolean res = fleet.addShip(newShip);

            assertFalse(res, "Expected addShip to fail when position is already occupied by existing ship");
        }

        @Test
        @DisplayName("Path 3: Fail due to Collision Risk (tooCloseTo check)")
        void addShip_collisionRisk_preventsAdd() {
            // O teste original usava CollisionShip, mas este testa o caminho lógico normal.
            DummyShip existing = new DummyShip("A", Compass.NORTH, new Position(5, 5));
            fleet.getShips().add(existing);

            // Posição adjacente para forçar tooCloseTo = true
            DummyShip newShip = new DummyShip("B", Compass.NORTH, new Position(5, 6));
            newShip.addPos(new Position(5, 7)); // Garantir que tem mais de 1 pos para complexidade
            boolean res = fleet.addShip(newShip);

            assertFalse(res, "Expected addShip to fail due to collision risk (adjacent position)");
        }

        @Test
        @DisplayName("Path 4: Fail due to Size Limit")
        void addShip_sizeLimit_preventsAdd() {
            // Preenche a frota para exceder FLEET_SIZE para forçar a verificação de tamanho a falhar.
            for (int i = 0; i < fleetSize; i++) { // Preenche até FLEET_SIZE-1
                fleet.getShips().add(new DummyShip("T" + i, Compass.SOUTH, new Position(0, i)));
            }
            // Adiciona o último para atingir o limite
            fleet.getShips().add(new DummyShip("LAST", Compass.SOUTH, new Position(0, fleetSize)));

            boolean res = fleet.addShip(new DummyShip("NEW", Compass.WEST, new Position(1, 1)));
            assertFalse(res, "Expected addShip to fail when fleet size reaches IFleet.FLEET_SIZE");
        }

        // --- Testes de Limites ---

        @Test
        void addShip_outsideBoard_topNegative_preventsAdd() {
            // Cobre o ramo de limite superior
            DummyShip out = new DummyShip("OOB3", Compass.NORTH, new Position(-1, 0));
            assertFalse(fleet.addShip(out), "Expected addShip to fail for ship with negative row");
        }

        @Test
        void addShip_outsideBoard_bottomTooLarge_preventsAdd() {
            // Cobre o ramo de limite inferior
            DummyShip out = new DummyShip("OOB4", Compass.SOUTH, new Position(boardSize, 0));
            assertFalse(fleet.addShip(out), "Expected addShip to fail for ship with bottom row > BOARD_SIZE-1");
        }

        @Test
        void addShip_outsideBoard_leftNegative_preventsAdd() {
            // Cobre o ramo de limite esquerdo
            DummyShip out = new DummyShip("OOB1", Compass.NORTH, new Position(0, -1));
            assertFalse(fleet.addShip(out), "Expected addShip to fail for ship with negative column");
        }

        @Test
        void addShip_outsideBoard_rightTooLarge_preventsAdd() {
            // Cobre o ramo de limite direito
            DummyShip out = new DummyShip("OOB2", Compass.EAST, new Position(1, boardSize));
            assertFalse(fleet.addShip(out), "Expected addShip to fail for ship with rightmost column > BOARD_SIZE-1");
        }

        @Test
        void addShip_null_throwsNPE() {
            assertThrows(NullPointerException.class, () -> fleet.addShip(null),
                    "Expected NullPointerException when calling addShip with null");
        }
    }

    // -------------------------------------------------------------

    @Nested
    @DisplayName("B. Ship Query Paths (Path Coverage)")
    class ShipQueryPaths {

        @Test
        @DisplayName("shipAt: Path 1: Found ship (loop break)")
        void shipAt_returnsCorrectShip_loopBreak() {
            DummyShip s = new DummyShip("S", Compass.NORTH, new Position(7, 7));
            fleet.getShips().add(s);
            IShip found = fleet.shipAt(new Position(7, 7));
            assertEquals(s, found, "Expected shipAt to return the ship occupying (7,7)");
        }

        @Test
        @DisplayName("shipAt: Path 2: Not found (loop complete)")
        void shipAt_returnsNull_loopComplete() {
            DummyShip s = new DummyShip("S", Compass.NORTH, new Position(7, 7));
            fleet.getShips().add(s);
            IShip none = fleet.shipAt(new Position(0, 0));
            assertNull(none, "Expected shipAt to return null when no ship occupies given position");
        }

        @Test
        @DisplayName("getShipsLike: Path 1: Found matching (loop continues/completes)")
        void getShipsLike_returnsMatchingCategoriesOnly() {
            DummyShip a1 = new DummyShip("A", Compass.NORTH, new Position(1, 1));
            DummyShip a2 = new DummyShip("A", Compass.NORTH, new Position(2, 2));
            DummyShip b = new DummyShip("B", Compass.SOUTH, new Position(3, 3));
            fleet.getShips().add(a1);
            fleet.getShips().add(a2);
            fleet.getShips().add(b);
            List<IShip> likeA = fleet.getShipsLike("A");
            assertEquals(2, likeA.size(), "Expected 2 ships for category 'A'");
        }

        @Test
        @DisplayName("getShipsLike: Path 2: Not found (loop completes)")
        void getShipsLike_emptyString_returnsEmpty_loopComplete() {
            DummyShip a = new DummyShip("X", Compass.NORTH, new Position(1, 1));
            fleet.getShips().add(a);
            List<IShip> like = fleet.getShipsLike("Y");
            assertTrue(like.isEmpty(), "Expected getShipsLike('Y') to return empty when no match");
        }

        @Test
        @DisplayName("getFloatingShips: Path 1: Found (loop continues/completes)")
        void getFloatingShips_returnsOnlyFloating() {
            DummyShip f1 = new DummyShip("F1", Compass.NORTH, new Position(1, 1));
            DummyShip f2 = new DummyShip("F2", Compass.NORTH, new Position(2, 2));
            f2.setFloating(false); // Sunk
            fleet.getShips().add(f1);
            fleet.getShips().add(f2);
            List<IShip> floating = fleet.getFloatingShips();
            assertEquals(1, floating.size(), "Expected only 1 floating ship");
            assertTrue(floating.contains(f1));
        }

        @Test
        @DisplayName("getFloatingShips: Path 2: None floating (loop completes)")
        void getFloatingShips_noneFloating_returnsEmpty() {
            DummyShip a = new DummyShip("A", Compass.NORTH, new Position(1, 1));
            a.setFloating(false);
            fleet.getShips().add(a);
            List<IShip> floating = fleet.getFloatingShips();
            assertTrue(floating.isEmpty(), "Expected no floating ships");
        }
    }

    // -------------------------------------------------------------

    @Nested
    @DisplayName("C. Printing and Miscellaneous Paths")
    class PrintingPaths {

        @Test
        void printShips_printsElements() {
            DummyShip s = new DummyShip("C", Compass.NORTH, new Position(1, 1));
            Fleet.printShips(List.of(s));
            assertTrue(outContent.toString().contains("C"));
        }

        @Test
        void printShips_null_throwsNPE() {
            assertThrows(NullPointerException.class, () -> Fleet.printShips(null));
        }

        @Test
        void printShips_emptyList_printsNothing() {
            Fleet.printShips(Collections.emptyList());
            assertTrue(outContent.toString().length() == 0);
        }

        @Test
        void printShipsByCategory_successPath() {
            // Adiciona um navio para garantir que o método tem algo para filtrar
            fleet.getShips().add(new DummyShip("Galeao", Compass.NORTH, new Position(1, 1)));

            // Chamada de sucesso para cobrir o ramo de impressão
            fleet.printShipsByCategory("Galeao");
            String out = outContent.toString();
            assertTrue(out.contains("Galeao"), "Expected category 'Galeao' in output");
        }

        @Test
        void printShipsByCategory_noMatchPath() {
            // Chamada sem correspondência para cobrir o ramo de lista vazia
            fleet.getShips().add(new DummyShip("Barca", Compass.NORTH, new Position(1, 1)));
            fleet.printShipsByCategory("Galeao");
            String out = outContent.toString();
            // A saída deve ser apenas a mensagem de debug/log do getShipsLike, mas não o navio.
            // Para ter certeza, verificamos que o output é pequeno ou não contém o navio errado.
            assertFalse(out.contains("Barca"), "Should not print unmatched category");
        }

        @Test
        void printStatus_invokesVariousPrints_noException() {
            fleet.getShips().add(new DummyShip("Galeao", Compass.NORTH, new Position(1, 1)));
            fleet.getShips().add(new DummyShip("Barca", Compass.SOUTH, new Position(2, 2)));
            fleet.printStatus();
            String out = outContent.toString();
            assertTrue(out.length() > 0, "Expected printStatus to write something to stdout");
        }

        // --- Testes originais para cobertura de instrução mantidos, mas movidos para a seção apropriada ---

        @Test
        void getShips_initiallyEmpty_thenReflectsAdds() {
            assertEquals(0, fleet.getShips().size());
            DummyShip s = new DummyShip("X", Compass.EAST, new Position(2, 2));
            fleet.addShip(s);
            assertTrue(fleet.getShips().contains(s));
        }
    }
}