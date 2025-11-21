package iscteiul.ista.battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

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



}
