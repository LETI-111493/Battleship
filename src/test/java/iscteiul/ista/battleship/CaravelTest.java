package iscteiul.ista.battleship;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void buildShip() {
        Ship built = Ship.buildShip("caravela", Compass.EAST, new Position(1, 1));
        assertNotNull(built,
                "Erro: Ship.buildShip('caravela') deveria devolver uma instância de Caravel, mas devolveu null.");
        assertEquals("Caravela", built.getCategory(),
                "Erro: Esperava-se que a categoria fosse 'Caravela', mas foi '" + built.getCategory() + "'.");
        assertEquals(2, built.getSize(),
                "Erro: Esperava-se que o tamanho da Caravela fosse 2, mas foi " + built.getSize() + ".");
    }

    @Test
    void getCategory() {
        assertEquals("Caravela", caravel.getCategory(),
                "Erro: getCategory() deveria devolver 'Caravela', mas devolveu '" + caravel.getCategory() + "'.");
    }

    @Test
    void getPositions() {
        List<IPosition> positions = caravel.getPositions();
        assertEquals(2, positions.size(),
                "Erro: A Caravela deveria ter exatamente 2 posições, mas a lista tem " + positions.size() + ".");

        IPosition p0 = positions.get(0);
        IPosition p1 = positions.get(1);

        assertEquals(initialPos, p0,
                "Erro: A primeira posição deveria ser a posição inicial (3,5), mas é " + p0 + ".");
        assertEquals(new Position(4, 5), p1,
                "Erro: A segunda posição deveria ser (4,5), mas é " + p1 + ".");

        // Cobrir também o ramo horizontal (EAST) do construtor
        Caravel eastCaravel = new Caravel(Compass.EAST, new Position(2, 2));
        List<IPosition> eastPos = eastCaravel.getPositions();
        assertEquals(2, eastPos.size(),
                "Erro: Caravela EAST deveria ter 2 posições, mas tem " + eastPos.size() + ".");
        assertEquals(new Position(2, 2), eastPos.get(0),
                "Erro: Para EAST, a primeira posição deveria ser (2,2), mas é " + eastPos.get(0) + ".");
        assertEquals(new Position(2, 3), eastPos.get(1),
                "Erro: Para EAST, a segunda posição deveria ser (2,3), mas é " + eastPos.get(1) + ".");
    }

    @Test
    void getPosition() {
        IPosition p = caravel.getPosition();
        assertEquals(initialPos, p,
                "Erro: getPosition() deveria devolver a posição inicial da Caravela (3,5), mas devolveu " + p + ".");
    }

    @Test
    void getBearing() {
        assertEquals(Compass.NORTH, caravel.getBearing(),
                "Erro: getBearing() deveria devolver NORTH, mas devolveu " + caravel.getBearing() + ".");
    }

    @Test
    void stillFloating() {
        // Inicialmente deve estar a flutuar
        assertTrue(caravel.stillFloating(),
                "Erro: Caravela recém-criada deveria estar a flutuar, mas stillFloating() devolveu false.");

        // Acertar apenas numa das posições → ainda flutua
        caravel.shoot(initialPos);
        assertTrue(caravel.stillFloating(),
                "Erro: Após um único tiro numa Caravela de tamanho 2, deveria ainda estar a flutuar.");

        // Acertar a segunda posição → deixa de flutuar
        caravel.shoot(new Position(4, 5));
        assertFalse(caravel.stillFloating(),
                "Erro: Após dois tiros nas duas posições da Caravela, deveria estar afundada, mas stillFloating() devolveu true.");
    }

    @Test
    void getTopMostPos() {
        assertEquals(3, caravel.getTopMostPos(),
                "Erro: getTopMostPos() deveria devolver 3 para uma Caravela vertical em (3,5)-(4,5), mas devolveu outro valor.");
    }

    @Test
    void getBottomMostPos() {
        assertEquals(4, caravel.getBottomMostPos(),
                "Erro: getBottomMostPos() deveria devolver 4 para uma Caravela vertical em (3,5)-(4,5), mas devolveu outro valor.");
    }

    @Test
    void getLeftMostPos() {
        assertEquals(5, caravel.getLeftMostPos(),
                "Erro: getLeftMostPos() deveria devolver 5 para uma Caravela em (3,5)-(4,5), mas devolveu outro valor.");
    }

    @Test
    void getRightMostPos() {
        assertEquals(5, caravel.getRightMostPos(),
                "Erro: getRightMostPos() deveria devolver 5 para uma Caravela em (3,5)-(4,5), mas devolveu outro valor.");
    }

    @Test
    void occupies() {
        // Ocupa ambas as posições
        assertTrue(caravel.occupies(initialPos),
                "Erro: occupies(initialPos) deveria ser true porque a Caravela ocupa essa posição.");
        assertTrue(caravel.occupies(new Position(4, 5)),
                "Erro: occupies(4,5) deveria ser true porque é a segunda posição da Caravela.");

        // Não ocupa posição distante
        assertFalse(caravel.occupies(new Position(0, 0)),
                "Erro: occupies(0,0) deveria ser false porque a Caravela não ocupa essa posição.");

        // Teste de exceção (assert) para argumento null
        assertThrows(AssertionError.class,
                () -> caravel.occupies(null),
                "Erro: occupies(null) deveria lançar AssertionError devido ao assert pos != null.");
    }

    @Test
    void tooCloseToPosition() {
        IPosition adjacent = new Position(3, 6);
        assertTrue(caravel.tooCloseTo(adjacent),
                "Erro: posição adjacente deveria ser considerada demasiado próxima.");

        IPosition far = new Position(10, 10);
        assertFalse(caravel.tooCloseTo(far),
                "Erro: posição distante não deveria ser considerada demasiado próxima.");

        // Null → NullPointerException
        assertThrows(NullPointerException.class,
                () -> caravel.tooCloseTo((IPosition) null),
                "Erro: tooCloseTo(null) deveria lançar NullPointerException.");
    }


    @Test
    void tooCloseToShip() {
        // Outra Caravela adjacente
        Ship near = new Caravel(Compass.NORTH, new Position(3, 6));
        Ship far = new Caravel(Compass.NORTH, new Position(10, 10));

        assertTrue(caravel.tooCloseTo(near),
                "Erro: Uma Caravela adjacente deveria ser considerada demasiado próxima, mas tooCloseTo(near) devolveu false.");

        assertFalse(caravel.tooCloseTo(far),
                "Erro: Uma Caravela distante não deveria ser considerada demasiado próxima, mas tooCloseTo(far) devolveu true.");

        // Null → AssertionError, por causa de assert other != null
        assertThrows(AssertionError.class,
                () -> caravel.tooCloseTo((Ship) null),
                "Erro: tooCloseTo(null) com IShip deveria lançar AssertionError devido ao assert other != null.");
    }

    @Test
    void shoot() {
        // Tiro falhado
        caravel.shoot(new Position(0, 0));
        assertTrue(caravel.stillFloating(),
                "Erro: Um tiro falhado não deveria afundar nem alterar o estado de flutuação da Caravela.");

        // Acertar as duas posições
        caravel.shoot(initialPos);
        assertTrue(caravel.stillFloating(),
                "Erro: Após acertar apenas uma das duas posições, a Caravela ainda deveria estar a flutuar.");

        caravel.shoot(new Position(4, 5));
        assertFalse(caravel.stillFloating(),
                "Erro: Após acertar as duas posições, a Caravela deveria estar afundada.");

        // Null → AssertionError por assert pos != null
        assertThrows(AssertionError.class,
                () -> caravel.shoot(null),
                "Erro: shoot(null) deveria lançar AssertionError devido ao assert pos != null.");
    }

    @Test
    void testToString() {
        String s = caravel.toString();
        assertNotNull(s,
                "Erro: toString() não deveria devolver null.");
        assertFalse(s.isEmpty(),
                "Erro: toString() não deveria devolver uma String vazia.");
        assertTrue(s.contains("Caravela"),
                "Erro: toString() deveria conter o nome 'Caravela', mas devolveu '" + s + "'.");
    }

    @Test
    void getSize() {
        assertEquals(2, caravel.getSize(),
                "Erro: getSize() deveria devolver 2 para a Caravela, mas devolveu " + caravel.getSize() + ".");
    }

    @Test
    void constructorNullBearingThrows() {
        assertThrows(AssertionError.class,
                () -> new Caravel(null, new Position(0, 0)),
                "Erro: new Caravel(null, pos) deveria lançar AssertionError devido ao assert bearing != null no construtor de Ship.");
    }

    @Nested
    @DisplayName("tooCloseTo(IPosition) - decomposição de A (linha) e B (coluna)")
    class TooCloseToPositionConditions {

        @Test
        @DisplayName("A=true && B=true -> retorna true (adjacente)")
        void aTrue_bTrue_returnsTrue() {
            Caravel c = new Caravel(Compass.NORTH, new Position(3, 5));
            // mesma linha e coluna +1 -> A true (0<=1), B true (1<=1)
            assertTrue(c.tooCloseTo(new Position(3, 6)));
        }

        @Test
        @DisplayName("A=true && B=false -> retorna false (linha próxima, coluna distante)")
        void aTrue_bFalse_returnsFalse() {
            Caravel c = new Caravel(Compass.NORTH, new Position(3, 5));
            // linha difere em 1 (A true), coluna difere em 3 (B false)
            assertFalse(c.tooCloseTo(new Position(4, 8)));
        }

        @Test
        @DisplayName("A=false && B=true -> retorna false (linha distante, coluna próxima)")
        void aFalse_bTrue_returnsFalse() {
            Caravel c = new Caravel(Compass.NORTH, new Position(3, 5));
            // linha difere em 4 (A false), coluna difere em 1 (B true)
            assertFalse(c.tooCloseTo(new Position(7, 6)));
        }

        @Test
        @DisplayName("A=false && B=false -> retorna false (distante em ambos)")
        void aFalse_bFalse_returnsFalse() {
            Caravel c = new Caravel(Compass.NORTH, new Position(3, 5));
            assertFalse(c.tooCloseTo(new Position(10, 10)));
        }

        @Test
        @DisplayName("pos null -> lança NullPointerException")
        void positionNull_throwsNPE() {
            Caravel c = new Caravel(Compass.NORTH, new Position(3, 5));
            assertThrows(NullPointerException.class, () -> c.tooCloseTo((IPosition) null));
        }
    }

    @Nested
    @DisplayName("tooCloseTo(Ship) - decomposição de 'other != null' e proximidade de posições")
    class TooCloseToShipConditions {

        @Test
        @DisplayName("other == null -> lança AssertionError (condição atómica false)")
        void otherNull_throwsAssertionError() {
            Caravel c = new Caravel(Compass.NORTH, new Position(3, 5));
            assertThrows(AssertionError.class, () -> c.tooCloseTo((Ship) null));
        }

        @Test
        @DisplayName("other != null && existe posição adjacente -> true")
        void otherNotNull_and_adjacentPosition_returnsTrue() {
            Caravel c = new Caravel(Compass.NORTH, new Position(3, 5));
            Ship other = new Caravel(Compass.NORTH, new Position(3, 6)); // adjacente
            assertTrue(c.tooCloseTo(other));
        }

        @Test
        @DisplayName("other != null && nenhuma posição adjacente -> false")
        void otherNotNull_and_noAdjacentPosition_returnsFalse() {
            Caravel c = new Caravel(Compass.NORTH, new Position(3, 5));
            Ship other = new Caravel(Compass.NORTH, new Position(10, 10)); // distante
            assertFalse(c.tooCloseTo(other));
        }

        @Test
        @DisplayName("other com múltiplas posições: uma adjacente basta -> true")
        void otherHasMultiplePositions_oneAdjacent_returnsTrue() {
            Caravel c = new Caravel(Compass.NORTH, new Position(3, 5));
            // cria navio com uma posição adjacente e outra distante
            Ship other = new Caravel(Compass.EAST, new Position(2, 6)); // positions: (2,6) e (2,7)
            assertTrue(c.tooCloseTo(other));
        }
    }

    @Nested
    @DisplayName("occupies(...) e shoot(...) - decomposição de pos != null e contains/ocupação")
    class OccupiesAndShootConditions {

        @Test
        @DisplayName("occupies: pos == null -> lança AssertionError")
        void occupies_posNull_throwsAssertionError() {
            Caravel c = new Caravel(Compass.NORTH, new Position(3, 5));
            assertThrows(AssertionError.class, () -> c.occupies(null));
        }

        @Test
        @DisplayName("occupies: pos presente -> true; pos ausente -> false")
        void occupies_presentAndAbsent() {
            Caravel c = new Caravel(Compass.NORTH, new Position(3, 5));
            assertTrue(c.occupies(new Position(3, 5)));
            assertFalse(c.occupies(new Position(0, 0)));
        }

        @Test
        @DisplayName("shoot: pos == null -> lança AssertionError (condição atómica false)")
        void shoot_posNull_throwsAssertionError() {
            Caravel c = new Caravel(Compass.NORTH, new Position(3, 5));
            assertThrows(AssertionError.class, () -> c.shoot(null));
        }

        @Test
        @DisplayName("shoot: pos não ocupada -> miss mantém stillFloating")
        void shoot_miss_keepsFloating() {
            Caravel c = new Caravel(Compass.NORTH, new Position(3, 5));
            boolean before = c.stillFloating();
            c.shoot(new Position(0, 0)); // miss
            assertEquals(before, c.stillFloating());
        }

        @Test
        @DisplayName("shoot: pos ocupada -> acerto; variando hits para verificar stillFloating")
        void shoot_hit_changesFloatingWhenAllHit() {
            Caravel c = new Caravel(Compass.NORTH, new Position(3, 5));
            // acerta primeira posição -> ainda flutua
            c.shoot(new Position(3, 5));
            assertTrue(c.stillFloating());
            // acerta segunda posição -> deixa de flutuar
            c.shoot(new Position(4, 5));
            assertFalse(c.stillFloating());
        }
    }


    @Test
    @DisplayName("Construtor SOUTH -> cria posições verticais (row, column) e tamanho 2")
    void constructorSouthCreatesVerticalPositions() {
        Caravel c = new Caravel(Compass.SOUTH, new Position(5, 7));
        List<IPosition> pos = c.getPositions();
        assertEquals(2, pos.size(), "Caravela SOUTH deve ter 2 posições");
        assertEquals(new Position(5, 7), pos.get(0), "primeira posição incorreta para SOUTH");
        assertEquals(new Position(6, 7), pos.get(1), "segunda posição incorreta para SOUTH");
    }

    @Test
    @DisplayName("Construtor WEST -> cria posições horizontais (row, column) e tamanho 2")
    void constructorWestCreatesHorizontalPositions() {
        Caravel c = new Caravel(Compass.WEST, new Position(8, 2));
        List<IPosition> pos = c.getPositions();
        assertEquals(2, pos.size(), "Caravela WEST deve ter 2 posições");
        assertEquals(new Position(8, 2), pos.get(0), "primeira posição incorreta para WEST");
        assertEquals(new Position(8, 3), pos.get(1), "segunda posição incorreta para WEST");
    }

}
