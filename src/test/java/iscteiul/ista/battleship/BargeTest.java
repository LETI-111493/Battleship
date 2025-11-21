// java
package iscteiul.ista.battleship;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes unitários para Barge (construtor e getSize)")
class BargeTest {

    private Barge barge;
    private IPosition initialPos;

    @BeforeEach
    void setUp() {
        // Cria uma Barge válida antes de cada teste que precisa de uma instância
        initialPos = new Position(3, 5);
        barge = new Barge(Compass.NORTH, initialPos);
    }

    @AfterEach
    void tearDown() {
        // Limpa referências após cada teste
        barge = null;
        initialPos = null;
    }

    @Nested
    @DisplayName("Testes do construtor de Barge")
    class ConstructorTests {

        @Test
        @DisplayName("Construtor válido não deve lançar exceção e deve inicializar corretamente")
        void constructor_valid_initializesCorrectly() {
            // Verifica que o construtor válido não lança e inicializa os campos esperados
            Barge b = assertDoesNotThrow(
                    () -> new Barge(Compass.SOUTH, new Position(0, 0)),
                    "Erro: construtor válido lançou exceção; esperado: nova instância criada sem lançar.");

            // Categoria deve ser 'Barca'
            assertEquals("Barca", b.getCategory(),
                    "Erro: categoria esperada 'Barca', mas obteve '" + b.getCategory() + "'.");

            // Deve conter exatamente uma posição igual à fornecida
            assertEquals(1, b.getPositions().size(),
                    "Erro: esperado 1 posição após construção, mas encontrou " + b.getPositions().size() + ".");
            assertEquals(new Position(0, 0), b.getPosition(),
                    "Erro: posição armazenada difere da posição inicial fornecida.");
        }

        @Test
        @DisplayName("Construtor com bearing nulo deve lançar AssertionError")
        void constructor_nullBearing_throwsAssertionError() {
            // Espera AssertionError quando bearing é null (caminho falso do precondicional)
            assertThrows(AssertionError.class,
                    () -> new Barge(null, new Position(1, 1)),
                    "Erro: esperado AssertionError ao chamar construtor com bearing == null, mas nenhuma AssertionError foi lançada.");
        }

        @Test
        @DisplayName("Construtor com posição nula deve lançar AssertionError")
        void constructor_nullPosition_throwsAssertionError() {
            // Espera AssertionError quando posição é null (caminho falso do precondicional)
            assertThrows(AssertionError.class,
                    () -> new Barge(Compass.EAST, null),
                    "Erro: esperado AssertionError ao chamar construtor com pos == null, mas nenhuma AssertionError foi lançada.");
        }
    }

    @Test
    @DisplayName("getSize deve devolver 1 para Barge")
    void getSize_returnsOne() {
        // Barge tem tamanho fixo 1
        assertEquals(Integer.valueOf(1), barge.getSize(),
                "Erro: esperado getSize() == 1 para Barge, mas obteve " + barge.getSize() + ".");
    }
}
