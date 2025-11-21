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

    // --- NOVOS TESTES PARA COBERTURA DE CAMINHO/EXCEÇÃO (CONSTRUTOR) ---

    @Test
    @DisplayName("Constructor throws AssertionError for null bearing")
    void constructorThrowsExceptionForNullBearing() {
        // Cobre o caminho de falha na pré-condição (bearing == null)
        assertThrows(AssertionError.class,
                () -> new Barge(null, new Position(0, 0)),
                "Erro: Construtor com bearing null deveria lançar AssertionError.");
    }

    @Test
    @DisplayName("Constructor throws AssertionError for null position")
    void constructorThrowsExceptionForNullPosition() {
        // Cobre o caminho de falha na pré-condição (pos == null)
        assertThrows(AssertionError.class,
                () -> new Barge(Compass.NORTH, null),
                "Erro: Construtor com posição inicial null deveria lançar AssertionError.");
    }

    // --- TESTES ORIGINAIS CONTINUAM AQUI ---

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
}