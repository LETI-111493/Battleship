package iscteiul.ista.battleship;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

    class BargeTest {

    private Barge barge;
    private IPosition initialPos;

    @BeforeEach
    void setUp() {
        initialPos = new Position(3, 5);
        barge = new Barge(Compass.NORTH, initialPos);
    }

    @AfterEach
    void tearDown() {
        barge = null;
        initialPos = null;
    }

    @Test
    void buildShip() {
        Ship built = Ship.buildShip("barca", Compass.EAST, new Position(1, 1));
        assertNotNull(built, "Erro: Ship.buildShip('barca') deveria devolver uma Barge, mas devolveu null.");
        assertEquals("Barca", built.getCategory(),
                "Erro: Esperava-se categoria 'Barca', mas foi '" + built.getCategory() + "'.");
    }

    @Test
    void getCategory() {
        assertEquals("Barca", barge.getCategory(),
                "Erro: getCategory() deveria devolver 'Barca', mas devolveu '" + barge.getCategory() + "'.");
    }

    @Test
    void getPositions() {
        List<IPosition> positions = barge.getPositions();
        assertEquals(1, positions.size(),
                "Erro: Barge deveria ter exatamente 1 posição; encontrou " + positions.size());
        assertEquals(initialPos, positions.get(0),
                "Erro: A posição armazenada não corresponde à posição inicial da barge.");
    }

    @Test
    void getPosition() {
        IPosition p = barge.getPosition();
        assertEquals(initialPos, p,
                "Erro: getPosition() deveria devolver a posição inicial da Barge.");
    }

    @Test
    void getBearing() {
        assertEquals(Compass.NORTH, barge.getBearing(),
                "Erro: getBearing deveria devolver NORTH, mas devolveu " + barge.getBearing());
    }

    @Test
    void stillFloating() {
        assertTrue(barge.stillFloating(),
                "Erro: A barge recém-criada deveria estar a flutuar.");

        barge.shoot(initialPos); // acerta
        assertFalse(barge.stillFloating(),
                "Erro: Após ser atingida na única posição, a Barge deveria estar afundada.");
    }

    @Test
    void getTopMostPos() {
        assertEquals(3, barge.getTopMostPos(),
                "Erro: getTopMostPos deveria devolver 3, mas devolveu outro valor.");
    }

    @Test
    void getBottomMostPos() {
        assertEquals(3, barge.getBottomMostPos(),
                "Erro: getBottomMostPos deveria devolver 3, mas devolveu outro valor.");
    }

    @Test
    void getLeftMostPos() {
        assertEquals(5, barge.getLeftMostPos(),
                "Erro: getLeftMostPos deveria devolver 5, mas devolveu outro valor.");
    }

    @Test
    void getRightMostPos() {
        assertEquals(5, barge.getRightMostPos(),
                "Erro: getRightMostPos deveria devolver 5, mas devolveu outro valor.");
    }

    @Test
    void occupies() {
        assertTrue(barge.occupies(initialPos),
                "Erro: occupies(initialPos) deveria ser true, pois a barge ocupa essa posição.");

        assertFalse(barge.occupies(new Position(0, 0)),
                "Erro: occupies(posição distante) deveria ser false.");

        assertThrows(AssertionError.class,
                () -> barge.occupies(null),
                "Erro: occupies(null) deveria lançar AssertionError devido ao assert pos != null.");
    }

    @Test
    void tooCloseToPosition() {
        // Posição adjacente (mesma linha, coluna +1)
        IPosition adjacent = new Position(initialPos.getRow(), initialPos.getColumn() + 1);
        assertTrue(barge.tooCloseTo(adjacent),
                "Erro: Uma posição adjacente deveria ser considerada demasiado próxima, mas tooCloseTo(adjacent) devolveu false.");

        // Posição distante
        IPosition far = new Position(initialPos.getRow() + 10, initialPos.getColumn() + 10);
        assertFalse(barge.tooCloseTo(far),
                "Erro: Uma posição distante não deveria ser considerada demasiado próxima, mas tooCloseTo(far) devolveu true.");

        // Null → NullPointerException (vem de Position.isAdjacentTo)
        assertThrows(NullPointerException.class,
                () -> barge.tooCloseTo((IPosition) null),
                "Erro: tooCloseTo(null) com IPosition deveria lançar NullPointerException.");
    }


    @Test
    void tooCloseToShip() {
        Ship near = new Barge(Compass.SOUTH, new Position(3, 6));
        Ship far = new Barge(Compass.SOUTH, new Position(10, 10));

        assertTrue(barge.tooCloseTo(near),
                "Erro: barcos adjacentes deveriam ser considerados demasiado próximos.");

        assertFalse(barge.tooCloseTo(far),
                "Erro: barcos distantes não deveriam ser considerados demasiado próximos.");

        assertThrows(AssertionError.class,
                () -> barge.tooCloseTo((Ship) null),
                "Erro: tooCloseTo(null) com IShip deveria lançar AssertionError.");
    }

    @Test
    void shoot() {
        barge.shoot(new Position(0, 0)); // falha
        assertTrue(barge.stillFloating(),
                "Erro: tiro falhado não deveria afundar a Barge.");

        barge.shoot(initialPos); // acerta
        assertFalse(barge.stillFloating(),
                "Erro: após ser atingida na única posição, a Barge deveria afundar.");

        assertThrows(AssertionError.class,
                () -> barge.shoot(null),
                "Erro: shoot(null) deveria lançar AssertionError.");
    }

    @Test
    void testToString() {
        String s = barge.toString();
        assertTrue(s.contains("Barca"),
                "Erro: toString() deveria conter o nome 'Barca'.");
    }

    @Test
    void getSize() {
        assertEquals(1, barge.getSize(),
                "Erro: getSize() deveria devolver 1 para uma Barge.");
    }

        @Nested
        @DisplayName("Construtor e factory")
        class ConstructorTests {


            @Test
            @DisplayName("Construtor: position nula deve lançar NullPointerException ou AssertionError")
            void constructorNullPositionThrowsNPEorAssertion() {
                // Esperado: NullPointerException ou AssertionError; Atual: exceção lançada pela construção
                Throwable thrown = assertThrows(Throwable.class,
                        () -> new Barge(Compass.NORTH, null),
                        "Esperava NullPointerException ou AssertionError ao criar Barge com pos == null, mas nenhuma exceção foi lançada");
                assertTrue(thrown instanceof NullPointerException || thrown instanceof AssertionError,
                        "Esperava NullPointerException ou AssertionError; Atual: " + thrown.getClass().getName());
            }

            @Test
            @DisplayName("Construtor: bearing nulo deve lançar NullPointerException ou AssertionError")
            void constructorNullBearingThrowsNPEorAssertion() {
                // Esperado: NullPointerException ou AssertionError; Atual: exceção lançada pela construção
                Throwable thrown = assertThrows(Throwable.class,
                        () -> new Barge(null, new Position(0, 0)),
                        "Esperava NullPointerException ou AssertionError ao criar Barge com bearing == null, mas nenhuma exceção foi lançada");
                assertTrue(thrown instanceof NullPointerException || thrown instanceof AssertionError,
                        "Esperava NullPointerException ou AssertionError; Atual: " + thrown.getClass().getName());
            }


            @Test
            @DisplayName("Ship.buildShip devolve Barge para nomes equivalentes (case-insensitive) e null para desconhecidos")
            void buildShipCaseInsensitiveAndUnknown() {
                Ship builtLower = Ship.buildShip("barca", Compass.EAST, new Position(1, 1));
                // Esperado: construir Barge mesmo com string em minúsculas; Atual: builtLower não nulo
                assertNotNull(builtLower, "Esperava Ship.buildShip(\"barca\") devolver instancia não-nula mas devolveu null");
                assertEquals("Barca", builtLower.getCategory(),
                        "Esperava categoria 'Barca' mas foi '" + builtLower.getCategory() + "'");

                Ship unknown = Ship.buildShip("cargueiro", Compass.EAST, new Position(1, 1));
                // Esperado: nomes desconhecidos devolvem null; Atual: unknown é null
                assertNull(unknown, "Esperava Ship.buildShip com nome desconhecido devolver null, mas devolveu instância");
            }
        }

        @Nested
        @DisplayName("Estado inicial e getters")
        class StateTests {

            @Test
            @DisplayName("Categoria, tamanho e posições iniciais estão corretos")
            void categorySizeAndPositions() {
                // Esperado: categoria 'Barca', tamanho 1 e única posição igual à inicial; Atual: valores obtidos
                assertEquals("Barca", barge.getCategory(),
                        "Esperava getCategory() == 'Barca' mas foi '" + barge.getCategory() + "'");
                assertEquals(1, barge.getSize(),
                        "Esperava getSize() == 1 mas foi " + barge.getSize());
                assertEquals(1, barge.getPositions().size(),
                        "Esperava 1 posição na barge mas encontrou " + barge.getPositions().size());
                assertEquals(initialPos, barge.getPositions().get(0),
                        "Esperava que a posição armazenada fosse a posição inicial; atual: " + barge.getPositions().get(0));
            }

            @Test
            @DisplayName("frontiers/top-bottom-left-right corretos para Barge de tamanho 1")
            void boundariesAreEqualForSingleCell() {
                // Esperado: top == bottom == row inicial, left == right == column inicial; Atual: valores retornados
                assertEquals(initialPos.getRow(), barge.getTopMostPos(),
                        "Esperava topMost == " + initialPos.getRow() + " mas foi " + barge.getTopMostPos());
                assertEquals(initialPos.getRow(), barge.getBottomMostPos(),
                        "Esperava bottomMost == " + initialPos.getRow() + " mas foi " + barge.getBottomMostPos());
                assertEquals(initialPos.getColumn(), barge.getLeftMostPos(),
                        "Esperava leftMost == " + initialPos.getColumn() + " mas foi " + barge.getLeftMostPos());
                assertEquals(initialPos.getColumn(), barge.getRightMostPos(),
                        "Esperava rightMost == " + initialPos.getColumn() + " mas foi " + barge.getRightMostPos());
            }
        }

        @Nested
        @DisplayName("Comportamento: ocupa, demasiado perto e tiro")
        class BehaviorTests {

            @Test
            @DisplayName("occupies devolve true apenas para a posição ocupada e lança AssertionError se null")
            void occupiesTrueFalseAndNull() {
                // Esperado: ocupa posição inicial -> true; Atual: resultado de occupies(initialPos)
                assertTrue(barge.occupies(initialPos),
                        "Esperava occupies(initialPos) == true mas foi false");
                // Esperado: ocupa posição distante -> false; Atual: resultado de occupies(new Position(0,0))
                assertFalse(barge.occupies(new Position(0, 0)),
                        "Esperava occupies(new Position(0,0)) == false mas foi true");
                // Esperado: occupies(null) lança AssertionError devido a assert pos != null; Atual: exceção lançada
                assertThrows(AssertionError.class,
                        () -> barge.occupies(null),
                        "Esperava AssertionError ao chamar occupies(null) mas nenhuma foi lançada");
            }

            @Test
            @DisplayName("tooCloseTo: posição e navio - caminhos true/false e tratamento de null")
            void tooCloseToPositionAndShipAndNull() {
                // Posição adjacente -> true
                IPosition adjacent = new Position(initialPos.getRow(), initialPos.getColumn() + 1);
                assertTrue(barge.tooCloseTo(adjacent),
                        "Esperava tooCloseTo(adjacent) == true mas foi false");

                // Posição distante -> false
                IPosition far = new Position(initialPos.getRow() + 10, initialPos.getColumn() + 10);
                assertFalse(barge.tooCloseTo(far),
                        "Esperava tooCloseTo(far) == false mas foi true");

                // tooCloseTo((Ship) null) -> AssertionError (validação de argumento)
                assertThrows(AssertionError.class,
                        () -> barge.tooCloseTo((Ship) null),
                        "Esperava AssertionError ao chamar tooCloseTo((Ship) null) mas nenhuma foi lançada");

                // tooCloseTo((IPosition) null) -> NullPointerException propagado de Position.isAdjacentTo
                assertThrows(NullPointerException.class,
                        () -> barge.tooCloseTo((IPosition) null),
                        "Esperava NullPointerException ao chamar tooCloseTo((IPosition) null) mas nenhuma foi lançada");
            }

            @Test
            @DisplayName("shoot: falha não afunda; acerto afunda; shoot(null) lança AssertionError")
            void shootMissHitAndNull() {
                // Tiro falhado -> ainda flutua
                barge.shoot(new Position(0, 0));
                assertTrue(barge.stillFloating(),
                        "Esperava stillFloating() == true após tiro falhado mas foi false");

                // Tiro acerta -> afunda
                barge.shoot(initialPos);
                assertFalse(barge.stillFloating(),
                        "Esperava stillFloating() == false após tiro que atinge a única posição mas foi true");

                // shoot(null) lança AssertionError (validação de argumento)
                assertThrows(AssertionError.class,
                        () -> barge.shoot(null),
                        "Esperava AssertionError ao chamar shoot(null) mas nenhuma foi lançada");
            }
        }
}
