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


        @Nested
        @DisplayName("Lógica booleana: occupies / shoot / stillFloating / tooCloseTo (IPosition)")
        class BooleanLogicTests {

            @Test
            @DisplayName("occupies: lança AssertionError quando pos é null")
            void occupies_null_throws() {
                assertThrows(AssertionError.class, () -> barge.occupies(null));
            }

            @Test
            @DisplayName("occupies: retorna true quando posição está presente e false quando ausente")
            void occupies_present_and_absent() {
                assertTrue(barge.occupies(initialPos), "posição inicial deve ser ocupada");
                assertFalse(barge.occupies(new Position(0, 0)), "posição distante não deve ser ocupada");
            }

            @Test
            @DisplayName("shoot: lança AssertionError quando pos é null; miss mantém flutuando; hit afunda")
            void shoot_null_miss_and_hit() {
                assertThrows(AssertionError.class, () -> barge.shoot(null));

                // miss -> não afunda
                barge.shoot(new Position(0, 0));
                assertTrue(barge.stillFloating(), "tiro falhado não deve afundar");

                // hit -> afunda
                barge.shoot(initialPos);
                assertFalse(barge.stillFloating(), "tiro acertado deve afundar");
            }



            @Test
            @DisplayName("stillFloating: lista vazia false; algum não atingido true; todos atingidos false")
            void stillFloating_conditions_decomposed() {
                // caso lista vazia -> usa uma instância controlada
                Ship empty = new Ship("testEmpty", Compass.NORTH, new Position(0, 0)) {
                    @Override
                    public Integer getSize() {
                        return getPositions().size();
                    }
                };
                empty.getPositions().clear();
                assertFalse(empty.stillFloating(), "lista vazia deve resultar em false");

                // usa uma instância separada para testar 'algum não atingido' e 'todos atingidos'
                Ship s = new Ship("test", Compass.NORTH, new Position(1, 1)) {
                    @Override
                    public Integer getSize() {
                        return getPositions().size();
                    }
                };
                s.getPositions().clear();
                Position p1 = new Position(1, 1);
                Position p2 = new Position(2, 2);
                s.getPositions().add(p1);
                s.getPositions().add(p2);

                // atinge apenas p1 -> ainda existe não atingido -> true
                s.shoot(p1);
                assertTrue(s.stillFloating(), "existe posição não atingida -> true");

                // atinge p2 também -> todos atingidos -> false
                s.shoot(p2);
                assertFalse(s.stillFloating(), "todos atingidos -> false");
            }



            @Test
            @DisplayName("tooCloseTo(IPosition): NullPointerException para null; adjacente true; distante false")
            void tooCloseTo_position_null_adjacent_and_far() {
                assertThrows(NullPointerException.class, () -> barge.tooCloseTo((IPosition) null));

                IPosition adjacent = new Position(initialPos.getRow(), initialPos.getColumn() + 1);
                assertTrue(barge.tooCloseTo(adjacent), "posição adjacente deve ser considerada demasiado próxima");

                IPosition far = new Position(100, 100);
                assertFalse(barge.tooCloseTo(far), "posição distante não deve ser considerada próxima");
            }
        }

        @Nested
        @DisplayName("Lógica booleana: tooCloseTo(IShip) e fluxo normal")
        class ShipProximityAndNormalFlowTests {

            @Test
            @DisplayName("tooCloseTo(IShip): lança AssertionError para ship null; adjacente true; distante false")
            void tooCloseTo_ship_null_adjacent_and_far() {
                assertThrows(AssertionError.class, () -> barge.tooCloseTo((Ship) null));

                Ship near = new Barge(Compass.SOUTH, new Position(initialPos.getRow(), initialPos.getColumn() + 1));
                assertTrue(barge.tooCloseTo(near), "navio adjacente deve ser considerado demasiado próximo");

                Ship far = new Barge(Compass.SOUTH, new Position(50, 50));
                assertFalse(barge.tooCloseTo(far), "navio distante não deve ser considerado próximo");
            }

            @Test
            @DisplayName("fluxo normal: getters e representação permanecem consistentes após operações")
            void normalFlow_getters_and_toString_after_operations() {
                // estado inicial
                assertEquals("Barca", barge.getCategory());
                assertEquals(Compass.NORTH, barge.getBearing());
                assertEquals(initialPos, barge.getPosition());

                // após operação de tiro falho e bem sucedido, toString ainda contém categoria
                barge.shoot(new Position(0, 0)); // miss
                barge.shoot(initialPos); // hit
                assertTrue(barge.toString().contains("Barca"), "toString deve continuar a conter a categoria");
            }
        }
}
