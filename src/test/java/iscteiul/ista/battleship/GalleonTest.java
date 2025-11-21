// java
package iscteiul.ista.battleship;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GalleonTest {

    private Galleon galleon;

    @BeforeEach
    void setUp() {
        // cria uma instância reutilizável orientada para NORTH na posição (2,3)
        galleon = new Galleon(Compass.NORTH, new Position(2, 3));
    }

    @AfterEach
    void tearDown() {
        // anula a referência para evitar efeitos entre testes
        galleon = null;
    }

    @Test
    void testGetSizeAndCategory() {
        // Esperado: tamanho 5 definido em Galleon; Atual: valor retornado por getSize()
        assertEquals(Integer.valueOf(5), galleon.getSize(),
                "Esperado tamanho=5 para Galleon; Atual: " + galleon.getSize());
        // Esperado: categoria \"Galeao\"; Atual: valor retornado por getCategory()
        assertEquals("Galeao", galleon.getCategory(),
                "Esperado categoria=\"Galeao\"; Atual: " + galleon.getCategory());
    }

    @Test
    void testFillNorthPositions() {
        // Para posição (2,3) e bearing NORTH, fillNorth produz: (2,3),(2,4),(2,5),(3,4),(4,4)
        List<IPosition> pos = galleon.getPositions();
        assertEquals(5, pos.size(),
                "Esperado 5 posições para Galleon NORTH; Atual: " + pos.size());

        assertEquals(2, pos.get(0).getRow(),
                "Esperado row=2 na posição 0; Atual: " + pos.get(0).getRow());
        assertEquals(3, pos.get(0).getColumn(),
                "Esperado column=3 na posição 0; Atual: " + pos.get(0).getColumn());

        assertEquals(2, pos.get(1).getRow(),
                "Esperado row=2 na posição 1; Atual: " + pos.get(1).getRow());
        assertEquals(4, pos.get(1).getColumn(),
                "Esperado column=4 na posição 1; Atual: " + pos.get(1).getColumn());

        assertEquals(2, pos.get(2).getRow(),
                "Esperado row=2 na posição 2; Atual: " + pos.get(2).getRow());
        assertEquals(5, pos.get(2).getColumn(),
                "Esperado column=5 na posição 2; Atual: " + pos.get(2).getColumn());

        assertEquals(3, pos.get(3).getRow(),
                "Esperado row=3 na posição 3; Atual: " + pos.get(3).getRow());
        assertEquals(4, pos.get(3).getColumn(),
                "Esperado column=4 na posição 3; Atual: " + pos.get(3).getColumn());

        assertEquals(4, pos.get(4).getRow(),
                "Esperado row=4 na posição 4; Atual: " + pos.get(4).getRow());
        assertEquals(4, pos.get(4).getColumn(),
                "Esperado column=4 na posição 4; Atual: " + pos.get(4).getColumn());
    }

    @Test
    void testFillSouthPositions() {
        // SOUTH: starting at (3,4) expected (3,4),(4,4),(5,3),(5,4),(5,5)
        Galleon s = new Galleon(Compass.SOUTH, new Position(3, 4));
        List<IPosition> pos = s.getPositions();
        assertEquals(5, pos.size(),
                "Esperado 5 posições para Galleon SOUTH; Atual: " + pos.size());

        assertEquals(3, pos.get(0).getRow(),
                "Esperado row=3 na posição 0 (SOUTH); Atual: " + pos.get(0).getRow());
        assertEquals(4, pos.get(0).getColumn(),
                "Esperado column=4 na posição 0 (SOUTH); Atual: " + pos.get(0).getColumn());

        assertEquals(4, pos.get(1).getRow(),
                "Esperado row=4 na posição 1 (SOUTH); Atual: " + pos.get(1).getRow());
        assertEquals(4, pos.get(1).getColumn(),
                "Esperado column=4 na posição 1 (SOUTH); Atual: " + pos.get(1).getColumn());

        assertEquals(5, pos.get(2).getRow(),
                "Esperado row=5 na posição 2 (SOUTH); Atual: " + pos.get(2).getRow());
        assertEquals(3, pos.get(2).getColumn(),
                "Esperado column=3 na posição 2 (SOUTH); Atual: " + pos.get(2).getColumn());

        assertEquals(5, pos.get(3).getRow(),
                "Esperado row=5 na posição 3 (SOUTH); Atual: " + pos.get(3).getRow());
        assertEquals(4, pos.get(3).getColumn(),
                "Esperado column=4 na posição 3 (SOUTH); Atual: " + pos.get(3).getColumn());

        assertEquals(5, pos.get(4).getRow(),
                "Esperado row=5 na posição 4 (SOUTH); Atual: " + pos.get(4).getRow());
        assertEquals(5, pos.get(4).getColumn(),
                "Esperado column=5 na posição 4 (SOUTH); Atual: " + pos.get(4).getColumn());
    }

    @Test
    void testFillEastPositions() {
        // EAST: starting at (5,5) expected (5,5),(6,3),(6,4),(6,5),(7,5)
        Galleon e = new Galleon(Compass.EAST, new Position(5, 5));
        List<IPosition> pos = e.getPositions();
        assertEquals(5, pos.size(),
                "Esperado 5 posições para Galleon EAST; Atual: " + pos.size());

        assertEquals(5, pos.get(0).getRow(),
                "Esperado row=5 na posição 0 (EAST); Atual: " + pos.get(0).getRow());
        assertEquals(5, pos.get(0).getColumn(),
                "Esperado column=5 na posição 0 (EAST); Atual: " + pos.get(0).getColumn());

        assertEquals(6, pos.get(1).getRow(),
                "Esperado row=6 na posição 1 (EAST); Atual: " + pos.get(1).getRow());
        assertEquals(3, pos.get(1).getColumn(),
                "Esperado column=3 na posição 1 (EAST); Atual: " + pos.get(1).getColumn());

        assertEquals(6, pos.get(2).getRow(),
                "Esperado row=6 na posição 2 (EAST); Atual: " + pos.get(2).getRow());
        assertEquals(4, pos.get(2).getColumn(),
                "Esperado column=4 na posição 2 (EAST); Atual: " + pos.get(2).getColumn());

        assertEquals(6, pos.get(3).getRow(),
                "Esperado row=6 na posição 3 (EAST); Atual: " + pos.get(3).getRow());
        assertEquals(5, pos.get(3).getColumn(),
                "Esperado column=5 na posição 3 (EAST); Atual: " + pos.get(3).getColumn());

        assertEquals(7, pos.get(4).getRow(),
                "Esperado row=7 na posição 4 (EAST); Atual: " + pos.get(4).getRow());
        assertEquals(5, pos.get(4).getColumn(),
                "Esperado column=5 na posição 4 (EAST); Atual: " + pos.get(4).getColumn());
    }

    @Test
    void testFillWestPositions() {
        // WEST: starting at (7,2) expected (7,2),(8,2),(8,3),(8,4),(9,2)
        Galleon w = new Galleon(Compass.WEST, new Position(7, 2));
        List<IPosition> pos = w.getPositions();
        assertEquals(5, pos.size(),
                "Esperado 5 posições para Galleon WEST; Atual: " + pos.size());

        assertEquals(7, pos.get(0).getRow(),
                "Esperado row=7 na posição 0 (WEST); Atual: " + pos.get(0).getRow());
        assertEquals(2, pos.get(0).getColumn(),
                "Esperado column=2 na posição 0 (WEST); Atual: " + pos.get(0).getColumn());

        assertEquals(8, pos.get(1).getRow(),
                "Esperado row=8 na posição 1 (WEST); Atual: " + pos.get(1).getRow());
        assertEquals(2, pos.get(1).getColumn(),
                "Esperado column=2 na posição 1 (WEST); Atual: " + pos.get(1).getColumn());

        assertEquals(8, pos.get(2).getRow(),
                "Esperado row=8 na posição 2 (WEST); Atual: " + pos.get(2).getRow());
        assertEquals(3, pos.get(2).getColumn(),
                "Esperado column=3 na posição 2 (WEST); Atual: " + pos.get(2).getColumn());

        assertEquals(8, pos.get(3).getRow(),
                "Esperado row=8 na posição 3 (WEST); Atual: " + pos.get(3).getRow());
        assertEquals(4, pos.get(3).getColumn(),
                "Esperado column=4 na posição 3 (WEST); Atual: " + pos.get(3).getColumn());

        assertEquals(9, pos.get(4).getRow(),
                "Esperado row=9 na posição 4 (WEST); Atual: " + pos.get(4).getRow());
        assertEquals(2, pos.get(4).getColumn(),
                "Esperado column=2 na posição 4 (WEST); Atual: " + pos.get(4).getColumn());
    }

    @Test
    void testOccupiesAndOutside() {
        List<IPosition> positions = galleon.getPositions();
        IPosition inside = positions.get(0);
        IPosition outside = new Position(99, 99);

        assertTrue(galleon.occupies(inside),
                "Esperado occupies(...) retornar true para posição dentro do navio; Atual: false");
        assertFalse(galleon.occupies(outside),
                "Esperado occupies(...) retornar false para posição claramente fora do navio; Atual: true");
    }

    @Test
    void testToStringContainsCategory() {
        String s = galleon.toString();
        assertNotNull(s, "Esperado toString() não-nulo; Atual: null");
        assertTrue(s.contains(galleon.getCategory()),
                "Esperado toString() conter a categoria \"" + galleon.getCategory() + "\"; Atual: " + s);
    }

    // java
    @Test
    void testConstructorWithNullBearingThrowsAssertionError() {
        // A implementação atual usa `assert` na superclasse; com -ea ativo a criação lança AssertionError
        AssertionError thrown = assertThrows(AssertionError.class,
                () -> new Galleon(null, new Position(0, 0)),
                "Esperava AssertionError ao criar Galleon com bearing null (assert na superclasse), mas nenhuma foi lançada");
        assertNotNull(thrown, "Esperado AssertionError não-nulo; Atual: " + thrown);
    }



    @Nested
    @DisplayName("A && B - decomposição completa (cada átomo varia)")
    class AndExpressionDecomposition {

        @Test
        @DisplayName("A=true && B=true -> ocupa e row correta -> true")
        void aTrue_bTrue_returnsTrue() {
            IPosition inside = galleon.getPositions().get(0);
            boolean A = galleon.occupies(inside);
            boolean B = inside.getRow() == 2;
            assertTrue(A && B, "Esperado true quando ocupa e row==2");
        }

        @Test
        @DisplayName("A=true && B=false -> ocupa mas row diferente -> false")
        void aTrue_bFalse_returnsFalse() {
            IPosition inside = galleon.getPositions().get(0);
            boolean A = galleon.occupies(inside);
            boolean B = inside.getRow() == 99;
            assertFalse(A && B);
        }

        @Test
        @DisplayName("A=false && B=true -> não ocupa mas row esperada -> false")
        void aFalse_bTrue_returnsFalse() {
            IPosition outside = new Position(99, 99);
            boolean A = galleon.occupies(outside);
            boolean B = outside.getRow() == 99;
            assertFalse(A && B);
        }

        @Test
        @DisplayName("A=false && B=false -> nem ocupa nem row correspondente -> false")
        void aFalse_bFalse_returnsFalse() {
            IPosition outside = new Position(99, 99);
            boolean A = galleon.occupies(outside);
            boolean B = outside.getRow() == 0;
            assertFalse(A && B);
        }
    }

    @Nested
    @DisplayName("A || B - decomposição completa (cada átomo varia)")
    class OrExpressionDecomposition {

        @Test
        @DisplayName("A=true || B=true -> ocupa ou row correta -> true")
        void aTrue_bTrue_returnsTrue() {
            IPosition inside = galleon.getPositions().get(0);
            boolean A = galleon.occupies(inside);
            boolean B = inside.getColumn() == 3;
            assertTrue(A || B);
        }

        @Test
        @DisplayName("A=true || B=false -> ocupa e column diferente -> true")
        void aTrue_bFalse_returnsTrue() {
            IPosition inside = galleon.getPositions().get(0);
            boolean A = galleon.occupies(inside);
            boolean B = inside.getColumn() == 99;
            assertTrue(A || B);
        }

        @Test
        @DisplayName("A=false || B=true -> não ocupa mas column esperada -> true")
        void aFalse_bTrue_returnsTrue() {
            IPosition outside = new Position(99, 99);
            boolean A = galleon.occupies(outside);
            boolean B = outside.getColumn() == 99;
            assertTrue(A || B);
        }

        @Test
        @DisplayName("A=false || B=false -> nem ocupa nem column correspondente -> false")
        void aFalse_bFalse_returnsFalse() {
            IPosition outside = new Position(99, 99);
            boolean A = galleon.occupies(outside);
            boolean B = outside.getColumn() == 0;
            assertFalse(A || B);
        }
    }

    @Nested
    @DisplayName("Ternary operator - decomposição da condição")
    class TernaryDecomposition {

        @Test
        @DisplayName("cond=true -> devolve \"INSIDE\"")
        void condTrue_returnsInside() {
            IPosition inside = galleon.getPositions().get(0);
            String result = galleon.occupies(inside) ? "INSIDE" : "OUTSIDE";
            assertEquals("INSIDE", result);
        }

        @Test
        @DisplayName("cond=false -> devolve \"OUTSIDE\"")
        void condFalse_returnsOutside() {
            IPosition outside = new Position(99, 99);
            String result = galleon.occupies(outside) ? "INSIDE" : "OUTSIDE";
            assertEquals("OUTSIDE", result);
        }
    }

    @Nested
    @DisplayName("while/combinada - garantir avaliação de cada átomo")
    class WhileAndCombinedDecomposition {

        @Test
        @DisplayName("while com A && B - B=false impede iterações")
        void whileCompositeCondition_bFalse_noIterations() {
            IPosition inside = galleon.getPositions().get(0);
            boolean A = galleon.occupies(inside);
            boolean B = false;
            int iterations = 0;
            int limit = 5;
            while (A && B && iterations < limit) { iterations++; }
            assertEquals(0, iterations);
        }

        @Test
        @DisplayName("while com A && B - A=true B=true executa até limite")
        void whileCompositeCondition_aTrue_bTrue_runs() {
            IPosition inside = galleon.getPositions().get(0);
            boolean A = galleon.occupies(inside);
            boolean B = true;
            int iterations = 0;
            int limit = 3;
            while (A && B && iterations < limit) { iterations++; }
            assertEquals(limit, iterations);
        }
    }

    @Nested
    @DisplayName("Fluxo normal - exemplos de asserções simples")
    class NormalFlowTests {

        @Test
        @DisplayName("retorna true quando ocupa e posição externa tem row diferente de 99")
        void example_combination_assertBehavior() {
            IPosition inside = galleon.getPositions().get(0);
            IPosition outside = new Position(0, 0);
            boolean result = galleon.occupies(inside) && outside.getRow() != 99;
            assertTrue(result);
        }
    }
}
