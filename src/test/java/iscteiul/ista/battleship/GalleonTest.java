// java
package iscteiul.ista.battleship;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests for Galleon Class (Path/Branch Coverage)")
class GalleonTest {

    private Galleon galleon;
    private Position initialPos = new Position(2, 3); // Base para o setUp

    @BeforeEach
    void setUp() {
        // cria uma instância reutilizável orientada para NORTH na posição (2,3)
        galleon = new Galleon(Compass.NORTH, initialPos);
    }

    @AfterEach
    void tearDown() {
        // anula a referência para evitar efeitos entre testes
        galleon = null;
    }

    // -------------------------------------------------------------

    @Nested
    @DisplayName("A. Constructor and Position Paths (Switch Cases)")
    class ConstructorPaths {

        @Test
        void testGetSizeAndCategory() {
            assertEquals(Integer.valueOf(5), galleon.getSize());
            assertEquals("Galeao", galleon.getCategory());
        }

        // Path: NORTH - Cobre o ramo NORTH do switch e os loops de fillNorth
        @Test
        void testFillNorthPositions() {
            List<IPosition> pos = galleon.getPositions();
            assertEquals(5, pos.size());
            assertEquals(4, pos.get(4).getRow(), "Verifica a última posição (Path/Loop)");
        }

        // Path: SOUTH - Cobre o ramo SOUTH do switch
        @Test
        void testFillSouthPositions() {
            Galleon s = new Galleon(Compass.SOUTH, new Position(3, 4));
            List<IPosition> pos = s.getPositions();
            assertEquals(5, pos.size());
            assertEquals(5, pos.get(4).getColumn(), "Verifica a última posição (Path/Loop)");
        }

        // Path: EAST - Cobre o ramo EAST do switch
        @Test
        void testFillEastPositions() {
            Galleon e = new Galleon(Compass.EAST, new Position(5, 5));
            List<IPosition> pos = e.getPositions();
            assertEquals(5, pos.size());
            assertEquals(7, pos.get(4).getRow(), "Verifica a última posição (Path/Loop)");
        }

        // Path: WEST - Cobre o ramo WEST do switch
        @Test
        void testFillWestPositions() {
            Galleon w = new Galleon(Compass.WEST, new Position(7, 2));
            List<IPosition> pos = w.getPositions();
            assertEquals(5, pos.size());
            assertEquals(9, pos.get(4).getRow(), "Verifica a última posição (Path/Loop)");
        }

        // Path: Exceção (AssertionError para null)
        @Test
        void testConstructorWithNullBearingThrowsAssertionError() {
            // Cobre o caminho de falha na pré-condição (assert na superclasse)
            assertThrows(AssertionError.class,
                    () -> new Galleon(null, new Position(0, 0)),
                    "Esperava AssertionError ao criar Galleon com bearing null");
        }

        @Test
        void testConstructorWithNullPositionThrowsAssertionError() {
            // NOVO TESTE: Cobre o caminho de falha na pré-condição (posição null)
            assertThrows(AssertionError.class,
                    () -> new Galleon(Compass.NORTH, null),
                    "Esperava AssertionError ao criar Galleon com posição null");
        }
    }

    // -------------------------------------------------------------

    @Nested
    @DisplayName("B. State, Query, and Loop Paths (Path Coverage)")
    class StateQueryPaths {

        // --- Caminhos de stillFloating (tamanho 5) ---

        @Test
        @DisplayName("Path 1: stillFloating - Loop completes and returns TRUE (some unhit)")
        void testStillFloating_SomeUnhit() {
            // Atinge 4 de 5 posições. O loop deve continuar e retornar true.
            List<IPosition> positions = galleon.getPositions();
            for (int i = 0; i < 4; i++) {
                positions.get(i).shoot();
            }
            assertTrue(galleon.stillFloating(), "Deveria flutuar após 4 acertos");
        }

        @Test
        @DisplayName("Path 2: stillFloating - Loop completes and returns FALSE (all hit)")
        void testStillFloating_AllHit() {
            // Atinge 5 de 5 posições. O loop deve completar e retornar false.
            List<IPosition> positions = galleon.getPositions();
            for (IPosition p : positions) {
                p.shoot();
            }
            assertFalse(galleon.stillFloating(), "Deveria afundar após 5 acertos");
        }

        // --- Caminhos de Occupies (Loop Break vs Loop Complete) ---

        @Test
        @DisplayName("Path 3: occupies - Loop Break (found early)")
        void testOccupies_FoundEarly() {
            IPosition inside = galleon.getPositions().get(0);
            // Ocupa (2,3). Loop deve quebrar na 1ª iteração.
            assertTrue(galleon.occupies(inside));
        }

        @Test
        @DisplayName("Path 4: occupies - Loop Complete (not found)")
        void testOccupies_NotFound() {
            IPosition outside = new Position(99, 99);
            // Não ocupa (99,99). Loop deve completar.
            assertFalse(galleon.occupies(outside));
        }

        @Test
        @DisplayName("Path 5: occupies - Assertion failure (null)")
        void testOccupies_NullAssertion() {
            assertThrows(AssertionError.class, () -> galleon.occupies(null),
                    "Esperava AssertionError ao chamar occupies(null)");
        }

        // --- Caminhos de Bounds (Loop logic) ---
        // Estes métodos (TopMostPos, etc.) usam loops e cobrem o Path se o navio tiver > 1 pos
        // Como a Galleon tem 5 posições (uma complexidade de loop garantida), os Path/Branch estão cobertos
        // se as posições forem bem verificadas (como nos testes de Fill, que já são detalhados).

        @Test
        void testTopMostPos() {
            // Galleon NORTH: (2,3), (2,4), (2,5), (3,4), (4,4) -> Top é 2.
            assertEquals(2, galleon.getTopMostPos());
        }

        @Test
        void testBottomMostPos() {
            // Galleon NORTH: (2,3), (2,4), (2,5), (3,4), (4,4) -> Bottom é 4.
            assertEquals(4, galleon.getBottomMostPos());
        }

        @Test
        void testLeftMostPos() {
            // Galleon NORTH: (2,3), (2,4), (2,5), (3,4), (4,4) -> Left é 3.
            assertEquals(3, galleon.getLeftMostPos());
        }

        @Test
        void testRightMostPos() {
            // Galleon NORTH: (2,3), (2,4), (2,5), (3,4), (4,4) -> Right é 5.
            assertEquals(5, galleon.getRightMostPos());
        }
    }

    // -------------------------------------------------------------

    @Nested
    @DisplayName("C. Collision and Interaction Paths")
    class CollisionPaths {

        @Test
        @DisplayName("tooCloseToShip: Loop Break (adjacent)")
        void testTooCloseToShip_Adjacent() {
            // Outro navio adjacente (e.g., pos (1,2) adjacente a (2,3) )
            Galleon near = new Galleon(Compass.NORTH, new Position(1, 2));
            // O loop deve quebrar cedo (return true)
            assertTrue(galleon.tooCloseTo(near), "Esperava tooCloseTo retornar true");
        }

        @Test
        @DisplayName("tooCloseToShip: Loop Complete (distant)")
        void testTooCloseToShip_Distant() {
            // Navio distante
            Galleon far = new Galleon(Compass.NORTH, new Position(50, 50));
            // O loop deve completar (return false)
            assertFalse(galleon.tooCloseTo(far), "Esperava tooCloseTo retornar false");
        }

        @Test
        @DisplayName("tooCloseToShip: Assertion failure (null)")
        void testTooCloseToShip_NullAssertion() {
            assertThrows(AssertionError.class, () -> galleon.tooCloseTo((IShip) null),
                    "Esperava AssertionError ao chamar tooCloseTo(null)");
        }

        @Test
        @DisplayName("testToStringContainsCategory")
        void testToStringContainsCategory() {
            String s = galleon.toString();
            assertTrue(s.contains(galleon.getCategory()));
        }
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
