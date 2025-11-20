package iscteiul.ista.battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CarrackTest {

    private Carrack carrack;
    private IPosition initialPos;

    @BeforeEach
    void setUp() {
        // Carrack vertical (NORTH) a partir de (3,5) → (3,5), (4,5), (5,5)
        initialPos = new Position(3, 5);
        carrack = new Carrack(Compass.NORTH, initialPos);
    }

    @AfterEach
    void tearDown() {
        carrack = null;
        initialPos = null;
    }

    @Test
    void buildShip() {
        Ship built = Ship.buildShip("nau", Compass.EAST, new Position(1, 1));

        assertNotNull(built,
                "Erro: Ship.buildShip('nau') deveria devolver uma instância de Carrack, mas devolveu null.");
        assertEquals("Nau", built.getCategory(),
                "Erro: Esperava-se que a categoria da Nau fosse 'Nau', mas foi '" + built.getCategory() + "'.");
        assertEquals(3, built.getSize(),
                "Erro: Esperava-se que o tamanho da Nau fosse 3, mas foi " + built.getSize() + ".");
    }

    @Test
    void getCategory() {
        assertEquals("Nau", carrack.getCategory(),
                "Erro: getCategory() deveria devolver 'Nau', mas devolveu '" + carrack.getCategory() + "'.");
    }

    @Test
    void getPositions() {
        List<IPosition> positions = carrack.getPositions();
        assertEquals(3, positions.size(),
                "Erro: A Nau deveria ter exatamente 3 posições, mas a lista tem " + positions.size() + ".");

        IPosition p0 = positions.get(0);
        IPosition p1 = positions.get(1);
        IPosition p2 = positions.get(2);

        assertEquals(new Position(3, 5), p0,
                "Erro: A primeira posição da Nau (NORTH) deveria ser (3,5), mas foi " + p0 + ".");
        assertEquals(new Position(4, 5), p1,
                "Erro: A segunda posição da Nau (NORTH) deveria ser (4,5), mas foi " + p1 + ".");
        assertEquals(new Position(5, 5), p2,
                "Erro: A terceira posição da Nau (NORTH) deveria ser (5,5), mas foi " + p2 + ".");

        // Cobrir também o ramo horizontal (EAST) do construtor
        Carrack eastCarrack = new Carrack(Compass.EAST, new Position(2, 2));
        List<IPosition> eastPos = eastCarrack.getPositions();
        assertEquals(3, eastPos.size(),
                "Erro: A Nau com bearing EAST deveria ter 3 posições, mas a lista tem " + eastPos.size() + ".");
        assertEquals(new Position(2, 2), eastPos.get(0),
                "Erro: Para EAST, a primeira posição deveria ser (2,2), mas foi " + eastPos.get(0) + ".");
        assertEquals(new Position(2, 3), eastPos.get(1),
                "Erro: Para EAST, a segunda posição deveria ser (2,3), mas foi " + eastPos.get(1) + ".");
        assertEquals(new Position(2, 4), eastPos.get(2),
                "Erro: Para EAST, a terceira posição deveria ser (2,4), mas foi " + eastPos.get(2) + ".");
    }

    @Test
    void getPosition() {
        IPosition p = carrack.getPosition();
        assertEquals(initialPos, p,
                "Erro: getPosition() deveria devolver a posição inicial (3,5) da Nau, mas devolveu " + p + ".");
    }

    @Test
    void getBearing() {
        assertEquals(Compass.NORTH, carrack.getBearing(),
                "Erro: getBearing() deveria devolver NORTH para a Nau criada no setUp, mas devolveu " + carrack.getBearing() + ".");
    }

    @Test
    void stillFloating() {
        // Inicialmente deve estar a flutuar
        assertTrue(carrack.stillFloating(),
                "Erro: Uma Nau recém-criada deveria estar a flutuar, mas stillFloating() devolveu false.");

        // Acertar apenas uma posição → ainda flutua
        carrack.shoot(new Position(3, 5));
        assertTrue(carrack.stillFloating(),
                "Erro: Após um único tiro numa Nau de tamanho 3, ainda deveria estar a flutuar, mas stillFloating() devolveu false.");

        // Acertar segunda posição → ainda flutua
        carrack.shoot(new Position(4, 5));
        assertTrue(carrack.stillFloating(),
                "Erro: Após dois tiros (em 3,5 e 4,5), a Nau ainda deveria estar a flutuar, mas stillFloating() devolveu false.");

        // Acertar terceira posição → deixa de flutuar
        carrack.shoot(new Position(5, 5));
        assertFalse(carrack.stillFloating(),
                "Erro: Após três tiros em todas as posições da Nau, ela deveria estar afundada, mas stillFloating() devolveu true.");
    }

    @Test
    void getTopMostPos() {
        assertEquals(3, carrack.getTopMostPos(),
                "Erro: getTopMostPos() deveria devolver 3 para uma Nau vertical em (3,5)-(4,5)-(5,5), mas devolveu outro valor.");
    }

    @Test
    void getBottomMostPos() {
        assertEquals(5, carrack.getBottomMostPos(),
                "Erro: getBottomMostPos() deveria devolver 5 para uma Nau vertical em (3,5)-(4,5)-(5,5), mas devolveu outro valor.");
    }

    @Test
    void getLeftMostPos() {
        assertEquals(5, carrack.getLeftMostPos(),
                "Erro: getLeftMostPos() deveria devolver 5 para uma Nau em (3,5)-(4,5)-(5,5), mas devolveu outro valor.");
    }

    @Test
    void getRightMostPos() {
        assertEquals(5, carrack.getRightMostPos(),
                "Erro: getRightMostPos() deveria devolver 5 para uma Nau em (3,5)-(4,5)-(5,5), mas devolveu outro valor.");
    }

    @Test
    void occupies() {
        // Ocupa as três posições
        assertTrue(carrack.occupies(new Position(3, 5)),
                "Erro: occupies(3,5) deveria ser true, pois é a primeira posição da Nau.");
        assertTrue(carrack.occupies(new Position(4, 5)),
                "Erro: occupies(4,5) deveria ser true, pois é a segunda posição da Nau.");
        assertTrue(carrack.occupies(new Position(5, 5)),
                "Erro: occupies(5,5) deveria ser true, pois é a terceira posição da Nau.");

        // Não ocupa posição distante
        assertFalse(carrack.occupies(new Position(0, 0)),
                "Erro: occupies(0,0) deveria ser false, pois a Nau não ocupa essa posição.");

        // Null → AssertionError pelo assert pos != null em Ship.occupies
        assertThrows(AssertionError.class,
                () -> carrack.occupies(null),
                "Erro: occupies(null) deveria lançar AssertionError devido ao assert pos != null.");
    }

    @Test
    void tooCloseToPosition() {
        IPosition adjacent = new Position(3, 6);
        assertTrue(carrack.tooCloseTo(adjacent),
                "Erro: posição adjacente deveria ser considerada demasiado próxima.");

        IPosition far = new Position(10, 10);
        assertFalse(carrack.tooCloseTo(far),
                "Erro: posição distante não deveria ser considerada demasiado próxima.");

        // Null → NullPointerException
        assertThrows(NullPointerException.class,
                () -> carrack.tooCloseTo((IPosition) null),
                "Erro: tooCloseTo(null) deveria lançar NullPointerException.");
    }


    @Test
    void tooCloseToShip() {
        // Outra Nau adjacente
        Ship near = new Carrack(Compass.NORTH, new Position(3, 6));
        Ship far = new Carrack(Compass.NORTH, new Position(10, 10));

        assertTrue(carrack.tooCloseTo(near),
                "Erro: Uma Nau adjacente deveria ser considerada demasiado próxima, mas tooCloseTo(near) devolveu false.");
        assertFalse(carrack.tooCloseTo(far),
                "Erro: Uma Nau distante não deveria ser considerada demasiado próxima, mas tooCloseTo(far) devolveu true.");

        // Null → AssertionError por causa de assert other != null
        assertThrows(AssertionError.class,
                () -> carrack.tooCloseTo((Ship) null),
                "Erro: tooCloseTo(null) com IShip deveria lançar AssertionError devido ao assert other != null.");
    }

    @Test
    void shoot() {
        // Tiro falhado
        carrack.shoot(new Position(0, 0));
        assertTrue(carrack.stillFloating(),
                "Erro: Um tiro falhado não deveria afundar nem alterar o estado de flutuação da Nau.");

        // Tiros nas três posições
        carrack.shoot(new Position(3, 5));
        assertTrue(carrack.stillFloating(),
                "Erro: Após um único tiro na Nau (3,5), ela ainda deveria estar a flutuar.");

        carrack.shoot(new Position(4, 5));
        assertTrue(carrack.stillFloating(),
                "Erro: Após dois tiros (3,5 e 4,5), a Nau ainda deveria estar a flutuar.");

        carrack.shoot(new Position(5, 5));
        assertFalse(carrack.stillFloating(),
                "Erro: Após três tiros (3,5, 4,5 e 5,5), a Nau deveria estar afundada, mas stillFloating() devolveu true.");

        // Null → AssertionError pelo assert pos != null
        assertThrows(AssertionError.class,
                () -> carrack.shoot(null),
                "Erro: shoot(null) deveria lançar AssertionError devido ao assert pos != null.");
    }

    @Test
    void testToString() {
        String s = carrack.toString();
        assertNotNull(s,
                "Erro: toString() da Nau não deveria devolver null.");
        assertFalse(s.isEmpty(),
                "Erro: toString() da Nau não deveria devolver uma String vazia.");
        assertTrue(s.contains("Nau"),
                "Erro: toString() da Nau deveria conter o nome 'Nau', mas devolveu '" + s + "'.");
    }

    @Test
    void getSize() {
        assertEquals(3, carrack.getSize(),
                "Erro: getSize() deveria devolver 3 para a Nau, mas devolveu " + carrack.getSize() + ".");
    }

    @Test
    void constructorNullBearingThrows() {
        assertThrows(AssertionError.class,
                () -> new Carrack(null, new Position(0, 0)),
                "Erro: new Carrack(null, pos) deveria lançar AssertionError devido ao assert bearing != null no construtor de Ship.");
    }

}
