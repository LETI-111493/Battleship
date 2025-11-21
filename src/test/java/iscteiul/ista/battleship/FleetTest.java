package iscteiul.ista.battleship;

import org.junit.jupiter.api.*;

import java.util.List;

@DisplayName("Testes da classe Fleet")
class FleetTest {

    private Fleet fleet;

    @BeforeEach
    void setUp() {
        fleet = new Fleet();
    }

    @Test
    @DisplayName("Construtor cria frota vazia")
    void constructorCreatesEmptyFleet() {
        assertTrue(fleet.getShips().isEmpty());
    }

    @Test
    @DisplayName("addShip adiciona navio com sucesso")
    void addShipWorks() {
        IShip s = new Barge(Compass.EAST, new Position(1, 1));
        boolean added = fleet.addShip(s);

        assertTrue(added);
        assertEquals(1, fleet.getShips().size());
    }

    @Test
    @DisplayName("addShip falha quando há risco de colisão (testa colisionRisk indiretamente)")
    void addShipFailsWhenCollision() {
        IShip s1 = new Caravel(Compass.EAST, new Position(1, 1));
        IShip s2 = new Caravel(Compass.SOUTH, new Position(1, 1)); // colide

        assertTrue(fleet.addShip(s1));

        // aqui estamos a testar colisionRisk, mas indiretamente
        assertFalse(fleet.addShip(s2));
    }

    @Test
    @DisplayName("getFloatingShips devolve apenas navios flutuantes")
    void getFloatingShipsWorks() {
        IShip s = new Barge(Compass.EAST, new Position(2, 2));
        fleet.addShip(s);

        List<IShip> list = fleet.getFloatingShips();
        assertEquals(1, list.size());
    }

    @Test
    @DisplayName("getShips devolve todos os navios")
    void getShipsReturnsAll() {
        IShip s = new Barge(Compass.EAST, new Position(3, 3));
        fleet.addShip(s);

        assertEquals(1, fleet.getShips().size());
    }

    @Test
    @DisplayName("getShipsLike devolve navios de uma categoria")
    void getShipsLikeWorks() {
        IShip s = new Galleon(Compass.EAST, new Position(4, 4));
        fleet.addShip(s);

        List<IShip> list = fleet.getShipsLike("Galleon");
        assertEquals(1, list.size());
    }

    @Test
    @DisplayName("shipAt devolve o navio na posição correta")
    void shipAtWorks() {
        IShip s = new Barge(Compass.NORTH, new Position(3, 3));
        fleet.addShip(s);

        IShip found = fleet.shipAt(new Position(3, 3));

        assertNotNull(found);
    }

    @Test
    @DisplayName("Chamadas aos métodos de impressão não causam erro")
    void printMethodsRun() {
        IShip s = new Barge(Compass.EAST, new Position(1, 1));
        fleet.addShip(s);

        // só chamar para garantir cobertura de método
        fleet.printAllShips();
        fleet.printFloatingShips();
        fleet.printShips(fleet.getShips());
        fleet.printShipsByCategory("Barge");
        fleet.printStatus();
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
