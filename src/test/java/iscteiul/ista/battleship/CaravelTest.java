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
    @DisplayName("Construtor e factory")
    class ConstructorTests {

        @Test
        @DisplayName("Construtor: pos nula lança NullPointerException ou AssertionError")
        void constructorNullPositionThrows() {
            Throwable t = assertThrows(Throwable.class, () -> new Caravel(Compass.NORTH, null),
                    "Esperava exceção ao criar Caravel com pos == null, mas não foi lançada");
            assertTrue(t instanceof NullPointerException || t instanceof AssertionError,
                    "Esperava NullPointerException ou AssertionError; atual: " + t.getClass().getName());
        }

        @Test
        @DisplayName("Construtor: bearing nulo lança NullPointerException ou AssertionError")
        void constructorNullBearingThrows() {
            Throwable t = assertThrows(Throwable.class, () -> new Caravel(null, new Position(0, 0)),
                    "Esperava exceção ao criar Caravel com bearing == null, mas não foi lançada");
            assertTrue(t instanceof NullPointerException || t instanceof AssertionError,
                    "Esperava NullPointerException ou AssertionError; atual: " + t.getClass().getName());
        }

        @Test
        @DisplayName("Construtor: ramos NORTH/SOUTH e EAST/WEST produzem posições corretas")
        void constructorDirectionBranchesProduceCorrectPositions() {
            Caravel north = new Caravel(Compass.NORTH, new Position(3, 5));
            List<IPosition> np = north.getPositions();
            assertEquals(2, np.size(), "Esperava 2 posições para Caravel NORTH");
            assertEquals(new Position(3, 5), np.get(0));
            assertEquals(new Position(4, 5), np.get(1));

            Caravel east = new Caravel(Compass.EAST, new Position(2, 2));
            List<IPosition> ep = east.getPositions();
            assertEquals(2, ep.size(), "Esperava 2 posições para Caravel EAST");
            assertEquals(new Position(2, 2), ep.get(0));
            assertEquals(new Position(2, 3), ep.get(1));
        }
    }

    @Nested
    @DisplayName("Comportamento e mutações")
    class BehaviorTests {

        @Test
        @DisplayName("occupies: true/false e null -> AssertionError ou NPE")
        void occupiesTrueFalseAndNull() {
            Caravel c = new Caravel(Compass.NORTH, initialPos);
            assertTrue(c.occupies(initialPos), "Esperava occupies(initial) == true");
            assertTrue(c.occupies(new Position(initialPos.getRow() + 1, initialPos.getColumn())), "Esperava occupies(segundo) == true");
            assertFalse(c.occupies(new Position(0, 0)), "Esperava occupies(0,0) == false");

            Throwable t = assertThrows(Throwable.class, () -> c.occupies(null),
                    "Esperava exceção ao chamar occupies(null) mas nenhuma foi lançada");
            assertTrue(t instanceof AssertionError || t instanceof NullPointerException,
                    "Esperava AssertionError ou NullPointerException; atual: " + t.getClass().getName());
        }

        @Test
        @DisplayName("tooCloseTo: caminhos true/false para IPosition e Ship; nulls tratados")
        void tooCloseToPositionAndShipAndNull() {
            Caravel c = new Caravel(Compass.NORTH, initialPos);

                // posição adjacente -> true
            assertTrue(c.tooCloseTo(new Position(initialPos.getRow(), initialPos.getColumn() + 1)),
                    "Esperava tooCloseTo(adjacent) == true");

                // posição distante -> false
            assertFalse(c.tooCloseTo(new Position(50, 50)), "Esperava tooCloseTo(far) == false");

                // IPosition null -> provavelmente NullPointerException (propagado)
            assertThrows(NullPointerException.class, () -> c.tooCloseTo((IPosition) null),
                    "Esperava NullPointerException ao chamar tooCloseTo((IPosition) null)");

                // Ship null -> AssertionError (validação por assert no código base)
            Throwable t = assertThrows(Throwable.class, () -> c.tooCloseTo((Ship) null),
                    "Esperava exceção ao chamar tooCloseTo((Ship) null)");
            assertTrue(t instanceof AssertionError || t instanceof NullPointerException,
                    "Esperava AssertionError ou NullPointerException; atual: " + t.getClass().getName());
        }

        @Test
        @DisplayName("shoot: miss, hits e null -> comportamento e exceções")
        void shootMissHitAndNull() {
            Caravel c = new Caravel(Compass.NORTH, initialPos);

                // tiro falhado não afunda
            c.shoot(new Position(0, 0));
            assertTrue(c.stillFloating(), "Esperava stillFloating() == true após tiro falhado");

                // acertar ambas as posições afunda
            c.shoot(initialPos);
            assertTrue(c.stillFloating(), "Após 1 acerto numa Caravel de tamanho 2 ainda flutua");
            c.shoot(new Position(initialPos.getRow() + 1, initialPos.getColumn()));
            assertFalse(c.stillFloating(), "Após 2 acertos a Caravel deveria estar afundada");

                // shoot(null) -> AssertionError ou NPE
            Throwable t = assertThrows(Throwable.class, () -> c.shoot(null), "Esperava exceção ao chamar shoot(null)");
            assertTrue(t instanceof AssertionError || t instanceof NullPointerException,
                    "Esperava AssertionError ou NullPointerException; atual: " + t.getClass().getName());
        }
    }

    @Nested
    @DisplayName("Valores limite (boundary)")
    class BoundaryTests {

        @Test
        @DisplayName("Posições perto de Integer.MAX_VALUE são construídas sem overflow quando possível")
        void boundaryPositionsNearIntegerMax() {
                // escolhe valores que permitam add de +1 sem overflow
            int r = Integer.MAX_VALUE - 1;
            int c = Integer.MAX_VALUE - 1;

            Caravel v = new Caravel(Compass.NORTH, new Position(r, 0));
            List<IPosition> vp = v.getPositions();
            assertEquals(new Position(r, 0), vp.get(0));
            assertEquals(new Position(r + 1, 0), vp.get(1));

            Caravel h = new Caravel(Compass.EAST, new Position(0, c));
            List<IPosition> hp = h.getPositions();
            assertEquals(new Position(0, c), hp.get(0));
            assertEquals(new Position(0, c + 1), hp.get(1));
        }
    }

    // language: java
    @Test
    @DisplayName("Construtor: SOUTH e WEST produzem posições esperadas (ordem indiferente)")
    void constructorSouthAndWestProduceCorrectPositions() {
        // SOUTH: verifica presença da posição inicial e que a outra posição está adjacente na mesma coluna
        Caravel south = new Caravel(Compass.SOUTH, new Position(5, 5));
        List<IPosition> sp = south.getPositions();
        assertEquals(2, sp.size(), "Esperava 2 posições para Caravel SOUTH");
        // garante que uma das posições é a inicial
        assertTrue(sp.stream().anyMatch(p -> p.equals(new Position(5, 5))),
                "Esperava que uma das posições fosse (5,5), actual: " + sp);
        // identifica a posição que não é a inicial e valida adjacência vertical
        IPosition southOther = sp.get(0).equals(new Position(5, 5)) ? sp.get(1) : sp.get(0);
        assertEquals(5, southOther.getColumn(),
                "Esperava que a posição adjacente mantenha a mesma coluna (5), actual: " + southOther);
        assertEquals(1, Math.abs(southOther.getRow() - 5),
                "Esperava que a posição adjacente esteja a 1 linha de distância da inicial, actual: " + southOther);

        // WEST: verifica presença da posição inicial e que a outra posição está adjacente na mesma linha
        Caravel west = new Caravel(Compass.WEST, new Position(2, 4));
        List<IPosition> wp = west.getPositions();
        assertEquals(2, wp.size(), "Esperava 2 posições para Caravel WEST");
        assertTrue(wp.stream().anyMatch(p -> p.equals(new Position(2, 4))),
                "Esperava que uma das posições fosse (2,4), actual: " + wp);
        IPosition westOther = wp.get(0).equals(new Position(2, 4)) ? wp.get(1) : wp.get(0);
        assertEquals(2, westOther.getRow(),
                "Esperava que a posição adjacente mantenha a mesma row (2), actual: " + westOther);
        assertEquals(1, Math.abs(westOther.getColumn() - 4),
                "Esperava que a posição adjacente esteja a 1 coluna de distância da inicial, actual: " + westOther);
    }


    @Test
    @DisplayName("shoot: repetir tiro na mesma posição não conta como novo hit")
    void shootRepeatOnSamePositionDoesNotDoubleCount() {
        Caravel c = new Caravel(Compass.NORTH, initialPos);

        // Primeiro tiro na primeira posição -> 1 hit (ainda flutua)
        c.shoot(initialPos);
        assertTrue(c.stillFloating(), "Após um único hit a Caravela de tamanho 2 ainda deve flutuar");

        // Repetir o mesmo tiro -> não deve afundar nem alterar contagem de hits
        c.shoot(initialPos);
        assertTrue(c.stillFloating(), "Repetir tiro na mesma célula não deve afundar a Caravela");

        // Agora acertar a segunda posição -> deve afundar
        c.shoot(new Position(initialPos.getRow() + 1, initialPos.getColumn()));
        assertFalse(c.stillFloating(), "Após acertar ambas as posições a Caravela deve estar afundada");
    }

    @Test
    @DisplayName("tooCloseTo: outro navio sobrepondo ou distante (cobertura de ramos)")
    void tooCloseToOtherShipOverlappingAndFar() {
        Caravel c = new Caravel(Compass.NORTH, initialPos);

        // Outro navio que sobrepõe / está adjacente (posição (4,5) partilhada) -> true
        Ship overlapping = new Caravel(Compass.NORTH, new Position(4, 5));
        assertTrue(c.tooCloseTo(overlapping), "Navio que sobrepõe/adjacente deve ser considerado demasiado próximo");

        // Navio distante -> false
        Ship distant = new Caravel(Compass.NORTH, new Position(100, 100));
        assertFalse(c.tooCloseTo(distant), "Navio distante não deve ser considerado demasiado próximo");
    }

    @Test
    @DisplayName("occupies: segundo slot é ocupado e occupies(null) lança AssertionError ou NullPointerException")
    void occupiesSecondSlotAndNullVariant() {
        Caravel c = new Caravel(Compass.NORTH, initialPos);

        // Segunda posição também ocupada
        assertTrue(c.occupies(new Position(initialPos.getRow() + 1, initialPos.getColumn())),
                "A Caravela deve ocupar a segunda posição");

        // occupied(null) pode lançar AssertionError ou NullPointerException dependendo da implementação
        Throwable t = assertThrows(Throwable.class, () -> c.occupies(null),
                "Esperava exceção ao chamar occupies(null)");
        assertTrue(t instanceof AssertionError || t instanceof NullPointerException,
                "Esperava AssertionError ou NullPointerException; atual: " + t.getClass().getName());
    }


    @Test
    @DisplayName("tooCloseTo(IPosition): posição exactamente igual a uma célula do navio -> true")
    void tooCloseToExactPositionIsTrue() {
        Caravel c = new Caravel(Compass.NORTH, new Position(3, 5));
        // segunda posição é (4,5) para NORTH
        assertTrue(c.tooCloseTo(new Position(4, 5)),
                "Esperava tooCloseTo devolver true quando a posição é exactamente uma das posições do navio");
    }

    @Test
    @DisplayName("shoot após afundado é idempotente (não lança e mantém estado afundado)")
    void shootAfterSunkIsIdempotent() {
        Caravel c = new Caravel(Compass.NORTH, new Position(3, 5));
        // Afunda o navio
        c.shoot(new Position(3, 5));
        c.shoot(new Position(4, 5));
        assertFalse(c.stillFloating(), "Após dois acertos a Caravela deve estar afundada");
        // Disparar novamente nas mesmas posições não altera o estado e não lança
        c.shoot(new Position(3, 5));
        c.shoot(new Position(4, 5));
        assertFalse(c.stillFloating(), "Após tiros repetidos o estado deve permanecer afundado");
    }

    @Test
    @DisplayName("Fronteiras para Caravel horizontal (EAST) - top/bottom iguais, left/right distintos")
    void horizontalBoundsForEastCaravel() {
        Caravel e = new Caravel(Compass.EAST, new Position(2, 2));
        assertEquals(2, e.getTopMostPos(), "TopMost para Caravel horizontal deveria ser a linha inicial");
        assertEquals(2, e.getBottomMostPos(), "BottomMost para Caravel horizontal deveria ser a linha inicial");
        assertEquals(2, e.getLeftMostPos(), "LeftMost para Caravel EAST deveria ser a coluna inicial");
        assertEquals(3, e.getRightMostPos(), "RightMost para Caravel EAST deveria ser coluna inicial + 1");
    }

}
