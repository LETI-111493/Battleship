// java
package iscteiul.ista.battleship;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

    private Position pos;

    @BeforeEach
    void setUp() {
        // cria a instância usada em todos os testes
        pos = new Position(2, 3);
    }

    @AfterEach
    void tearDown() {
        // limpa a instância após cada teste
        pos = null;
    }

    @Test
    void getRow() {
        // Espera-se que getRow retorne o valor passado no construtor (2)
        assertEquals(2, pos.getRow(), "Esperava row = 2 mas foi diferente do esperado");
    }

    @Test
    void getColumn() {
        // Espera-se que getColumn retorne o valor passado no construtor (3)
        assertEquals(3, pos.getColumn(), "Esperava column = 3 mas foi diferente do esperado");
    }

    @Test
    void testHashCode() {
        // Dois objetos com mesmos row/column e mesmos flags devem ter o mesmo hashCode
        Position other = new Position(2, 3);
        assertEquals(pos.hashCode(), other.hashCode(), "Esperava hashCode igual para posições equivalentes mas foram diferentes");

        // Se um dos flags mudar (occupy), o hashCode deve mudar
        int before = pos.hashCode();
        pos.occupy();
        int after = pos.hashCode();
        assertNotEquals(before, after, "Esperava hashCode diferente após occupy() mas permaneceu igual");

        // Se marcar como hit também altera o hashCode (testa outro ramo)
        other = new Position(2, 3);
        int beforeOther = other.hashCode();
        other.shoot();
        int afterOther = other.hashCode();
        assertNotEquals(beforeOther, afterOther, "Esperava hashCode diferente após shoot() mas permaneceu igual");
    }

    @Test
    void testEquals() {
        // Igualdade com a própria referência
        assertTrue(pos.equals(pos), "Esperava equals(true) quando comparado com a própria instância mas retornou false");

        // Igualdade com outra Position com mesmos row/column
        Position same = new Position(2, 3);
        assertTrue(pos.equals(same), "Esperava equals(true) para posições com mesmos row/column mas retornou false");
        assertTrue(same.equals(pos), "Esperava simetria em equals mas a comparação inversa retornou false");

        // Diferentes coordenadas -> false
        Position differentRow = new Position(1, 3);
        assertFalse(pos.equals(differentRow), "Esperava equals(false) para row diferente mas retornou true");

        Position differentColumn = new Position(2, 4);
        assertFalse(pos.equals(differentColumn), "Esperava equals(false) para column diferente mas retornou true");

        // Comparar com null -> false (sem lançar exceção)
        assertFalse(pos.equals(null), "Esperava equals(false) quando comparado com null mas retornou true");

        // Comparar com objeto que NÃO implementa IPosition -> false
        Object notPosition = new Object();
        assertFalse(pos.equals(notPosition), "Esperava equals(false) quando comparado com objeto de outro tipo mas retornou true");

        // Comparar com objeto que IMPLEMENTA IPosition mas não é Position: equals deve usar getRow/getColumn -> true
        IPosition otherIPos = new IPosition() {
            @Override public int getRow() { return 2; }
            @Override public int getColumn() { return 3; }
            @Override public boolean isAdjacentTo(IPosition other) { return false; }
            @Override public void occupy() { /* no-op */ }
            @Override public void shoot() { /* no-op */ }
            @Override public boolean isOccupied() { return false; }
            @Override public boolean isHit() { return false; }
        };
        assertTrue(pos.equals(otherIPos), "Esperava equals(true) quando comparado com IPosition com mesmas coordenadas mas retornou false");
    }

    @Test
    void isAdjacentTo() {
        // Mesmo local -> adjacente
        assertTrue(pos.isAdjacentTo(new Position(2, 3)), "Esperava isAdjacentTo(true) para mesma posição mas retornou false");

        // Vizinhos vertical/horizontal/diagonal devem ser adjacentes
        assertTrue(pos.isAdjacentTo(new Position(1, 3)), "Esperava isAdjacentTo(true) para posição (1,3) mas retornou false");
        assertTrue(pos.isAdjacentTo(new Position(3, 3)), "Esperava isAdjacentTo(true) para posição (3,3) mas retornou false");
        assertTrue(pos.isAdjacentTo(new Position(2, 4)), "Esperava isAdjacentTo(true) para posição (2,4) mas retornou false");
        assertTrue(pos.isAdjacentTo(new Position(3, 4)), "Esperava isAdjacentTo(true) para posição diagonal (3,4) mas retornou false");

        // Posição com distância > 1 não é adjacente
        assertFalse(pos.isAdjacentTo(new Position(4, 3)), "Esperava isAdjacentTo(false) para posição (4,3) mas retornou true");
        assertFalse(pos.isAdjacentTo(new Position(0, 0)), "Esperava isAdjacentTo(false) para posição (0,0) mas retornou true");

        // Passar null deve lançar NullPointerException (outro ramo)
        assertThrows(NullPointerException.class, () -> pos.isAdjacentTo(null),
                "Esperava NullPointerException ao chamar isAdjacentTo(null) mas nenhuma exceção foi lançada");
    }

    @Test
    void occupy() {
        // Inicialmente não ocupado
        assertFalse(pos.isOccupied(), "Esperava isOccupied() == false imediatamente após criação mas retornou true");

        // Depois de occupy() -> isOccupied true
        pos.occupy();
        assertTrue(pos.isOccupied(), "Esperava isOccupied() == true após occupy() mas retornou false");
    }

    @Test
    void shoot() {
        // Inicialmente não atingido
        assertFalse(pos.isHit(), "Esperava isHit() == false imediatamente após criação mas retornou true");

        // Depois de shoot() -> isHit true
        pos.shoot();
        assertTrue(pos.isHit(), "Esperava isHit() == true após shoot() mas retornou false");
    }

    @Test
    void isOccupied() {
        // Verifica ramo padrão false -> true
        assertFalse(pos.isOccupied(), "Esperava isOccupied() inicialmente false mas retornou true");
        pos.occupy();
        assertTrue(pos.isOccupied(), "Esperava isOccupied() true após occupy() mas retornou false");
    }

    @Test
    void isHit() {
        // Verifica ramo padrão false -> true
        assertFalse(pos.isHit(), "Esperava isHit() inicialmente false mas retornou true");
        pos.shoot();
        assertTrue(pos.isHit(), "Esperava isHit() true após shoot() mas retornou false");
    }

    @Test
    void testToString() {
        // Deve corresponder exatamente ao formato implementado em Position.toString()
        String expected = "Linha = 2 Coluna = 3";
        assertEquals(expected, pos.toString(), "Esperava toString() com valor '" + expected + "' mas foi '" + pos.toString() + "'");
    }

    @Nested
    @DisplayName("Estado inicial e getters / toString")
    class InitialStateTests {

        @Test
        @DisplayName("getRow/getColumn e flags iniciais são corretos (estado padrão)")
        void testInitialGettersAndFlags() {
            // Esperado: getters retornam os valores do construtor; Atual: valores retornados por getRow/getColumn
            assertEquals(2, pos.getRow(), "Esperava row = 2 mas foi diferente do esperado");
            assertEquals(3, pos.getColumn(), "Esperava column = 3 mas foi diferente do esperado");
            // Esperado: flags iniciais false; Atual: isOccupied/isHit
            assertFalse(pos.isOccupied(), "Esperava isOccupied() == false imediatamente após criação mas retornou true");
            assertFalse(pos.isHit(), "Esperava isHit() == false imediatamente após criação mas retornou true");
        }

        @Test
        @DisplayName("toString deve seguir o formato implementado")
        void testToStringFormat() {
            String expected = "Linha = 2 Coluna = 3";
            // Esperado: string exatamente igual ao formato; Atual: pos.toString()
            assertEquals(expected, pos.toString(), "Esperava toString() com valor '" + expected + "' mas foi '" + pos.toString() + "'");
        }

        @Test
        @DisplayName("Construtor aceita valores limites (0, negativos, Integer.MAX_VALUE)")
        void testConstructorBoundaryValues() {
            Position zero = new Position(0, 0);
            // Esperado: aceita 0; Atual: getters retornam 0
            assertEquals(0, zero.getRow(), "Esperava row = 0 para boundary 0 mas foi diferente do esperado");
            assertEquals(0, zero.getColumn(), "Esperava column = 0 para boundary 0 mas foi diferente do esperado");

            Position negative = new Position(-1, -5);
            // Esperado: aceita negativos (sem validação); Atual: getters retornam valores negativos
            assertEquals(-1, negative.getRow(), "Esperava row = -1 para boundary negativo mas foi diferente do esperado");
            assertEquals(-5, negative.getColumn(), "Esperava column = -5 para boundary negativo mas foi diferente do esperado");

            Position max = new Position(Integer.MAX_VALUE, Integer.MAX_VALUE);
            // Esperado: aceita Integer.MAX_VALUE sem overflow; Atual: getters retornam Integer.MAX_VALUE
            assertEquals(Integer.MAX_VALUE, max.getRow(), "Esperava row = Integer.MAX_VALUE mas foi diferente do esperado");
            assertEquals(Integer.MAX_VALUE, max.getColumn(), "Esperava column = Integer.MAX_VALUE mas foi diferente do esperado");
        }
    }

    @Nested
    @DisplayName("Igualdade e hashCode")
    class EqualityAndHashTests {

        @Test
        @DisplayName("equals reflexivo, simétrico, e comparação com outro tipo retorna false")
        void testEqualsReflexiveSymmetricAndDifferentType() {
            // Reflexivo
            assertTrue(pos.equals(pos), "Esperava equals(true) quando comparado com a própria instância mas retornou false");

            // Simétrico com outra Position com mesmas coordenadas
            Position same = new Position(2, 3);
            assertTrue(pos.equals(same), "Esperava equals(true) para posições com mesmos row/column mas retornou false");
            assertTrue(same.equals(pos), "Esperava simetria em equals mas a comparação inversa retornou false");

            // Comparar com objeto de outro tipo -> false
            Object notPos = new Object();
            assertFalse(pos.equals(notPos), "Esperava equals(false) quando comparado com objeto de outro tipo mas retornou true");
        }

        @Test
        @DisplayName("equals com IPosition (objeto diferente que implementa IPosition) e com coordenadas diferentes")
        void testEqualsWithIPositionAndDifferentCoordinates() {
            // IPosition anónimo com mesmas coordenadas -> true
            IPosition otherIPos = new IPosition() {
                @Override public int getRow() { return 2; }
                @Override public int getColumn() { return 3; }
                @Override public boolean isAdjacentTo(IPosition other) { return false; }
                @Override public void occupy() { }
                @Override public void shoot() { }
                @Override public boolean isOccupied() { return false; }
                @Override public boolean isHit() { return false; }
            };
            assertTrue(pos.equals(otherIPos), "Esperava equals(true) ao comparar com IPosition com mesmas coordenadas mas retornou false");

            // Coordenadas diferentes -> false
            Position different = new Position(5, 3);
            assertFalse(pos.equals(different), "Esperava equals(false) para posições com coordenadas diferentes mas retornou true");
        }

        @Test
        @DisplayName("hashCode muda quando os flags isOccupied/isHit mudam")
        void testHashCodeChangesOnFlags() {
            Position other = new Position(2, 3);
            // Mesmo estado -> mesmos hashCodes
            assertEquals(other.hashCode(), pos.hashCode(), "Esperava hashCode igual para posições equivalentes mas foram diferentes");

            int before = pos.hashCode();
            pos.occupy(); // altera isOccupied -> afeta hashCode
            int after = pos.hashCode();
            assertNotEquals(before, after, "Esperava hashCode diferente após occupy() mas permaneceu igual");

            int before2 = other.hashCode();
            other.shoot(); // altera isHit -> afeta hashCode
            int after2 = other.hashCode();
            assertNotEquals(before2, after2, "Esperava hashCode diferente após shoot() mas permaneceu igual");
        }
    }

    @Nested
    @DisplayName("Adjacência e mutações (occupy/shoot)")
    class AdjacencyAndMutationsTests {

        @Test
        @DisplayName("isAdjacentTo retorna true para mesmas coordenadas e vizinhos, false para distâncias > 1")
        void testIsAdjacentToVarious() {
            // Mesmo local -> true
            assertTrue(pos.isAdjacentTo(new Position(2, 3)), "Esperava isAdjacentTo(true) para mesma posição mas retornou false");

            // Vizinhos (vertical/horizontal/diagonal) -> true
            assertTrue(pos.isAdjacentTo(new Position(1, 3)), "Esperava isAdjacentTo(true) para (1,3) mas retornou false");
            assertTrue(pos.isAdjacentTo(new Position(3, 3)), "Esperava isAdjacentTo(true) para (3,3) mas retornou false");
            assertTrue(pos.isAdjacentTo(new Position(2, 4)), "Esperava isAdjacentTo(true) para (2,4) mas retornou false");
            assertTrue(pos.isAdjacentTo(new Position(3, 4)), "Esperava isAdjacentTo(true) para diagonal (3,4) mas retornou false");

            // Distância maior que 1 -> false
            assertFalse(pos.isAdjacentTo(new Position(4, 3)), "Esperava isAdjacentTo(false) para (4,3) mas retornou true");
            assertFalse(pos.isAdjacentTo(new Position(0, 0)), "Esperava isAdjacentTo(false) para (0,0) mas retornou true");
        }

        @Test
        @DisplayName("isAdjacentTo com null lança NullPointerException")
        void testIsAdjacentToWithNullThrows() {
            // Esperado: NullPointerException ao passar null; Atual: exceção lançada ao acessar other.getRow()
            assertThrows(NullPointerException.class, () -> pos.isAdjacentTo(null),
                    "Esperava NullPointerException ao chamar isAdjacentTo(null) mas nenhuma exceção foi lançada");
        }

        @Test
        @DisplayName("occupy e shoot alteram os flags correspondentes")
        void testOccupyAndShootChangeFlags() {
            // Inicialmente false
            assertFalse(pos.isOccupied(), "Esperava isOccupied() inicialmente false mas retornou true");
            assertFalse(pos.isHit(), "Esperava isHit() inicialmente false mas retornou true");

            pos.occupy();
            assertTrue(pos.isOccupied(), "Esperava isOccupied() true após occupy() mas retornou false");

            pos.shoot();
            assertTrue(pos.isHit(), "Esperava isHit() true após shoot() mas retornou false");
        }

        @Test
        @DisplayName("isAdjacentTo: row adjacente (<=1) e column distante (>1) => false")
        void testIsAdjacentToRowAdjacentColumnFar() {
            // Esperado: row diff <= 1 (true) e column diff > 1 (false) => expressão AND avalia para false; Atual: resultado de isAdjacentTo(...)
            assertFalse(pos.isAdjacentTo(new Position(2, 6)),
                    "Esperava isAdjacentTo(false) quando row está adjacente mas column está a distância >1, mas retornou true");
        }

    }
}
