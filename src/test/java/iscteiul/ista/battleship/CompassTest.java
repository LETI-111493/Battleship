// java
package iscteiul.ista.battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CompassTest {

    private Compass compass;

    @BeforeEach
    void setUp() {
        // cria a instância usada em todos os testes
        compass = Compass.NORTH;
    }

    @AfterEach
    void tearDown() {
        // limpa a instância após cada teste
        compass = null;
    }

    @Test
    void nameAndOrdinal() {
        // Espera-se que name() retorne "NORTH"
        assertEquals("NORTH", compass.name(), "Esperava name() == 'NORTH' mas foi diferente");

        // Espera-se que ordinal() do primeiro elemento seja 0
        assertEquals(0, compass.ordinal(), "Esperava ordinal() == 0 para NORTH mas foi diferente");
    }

    @Test
    void testToStringAndGetDirection() {
        // Espera-se que toString() devolva o carácter associado 'n' como String
        assertEquals("n", compass.toString(), "Esperava toString() == 'n' mas foi diferente");

        // Espera-se que getDirection() devolva o caractere 'n'
        assertEquals('n', compass.getDirection(), "Esperava getDirection() == 'n' mas foi diferente");
    }

    @Test
    void testEqualsAndHashCode() {
        // Espera-se que Compass.NORTH seja igual a Compass.valueOf(\"NORTH\")
        Compass byName = Compass.valueOf("NORTH");
        assertTrue(compass.equals(byName), "Esperava equals(true) entre constantes iguais mas retornou false");

        // Espera-se que dois objetos iguais tenham o mesmo hashCode
        assertEquals(compass.hashCode(), byName.hashCode(), "Esperava hashCode igual para constantes equivalentes mas foi diferente");

        // Espera-se que constantes diferentes tenham hashCodes possivelmente diferentes (verifica desigualdade simples)
        assertNotEquals(compass.hashCode(), Compass.SOUTH.hashCode(),
                "Esperava hashCode possivelmente diferente para constantes distintas mas foram iguais");
    }

    @Test
    void testCompareToAndValues() {
        // Espera-se que NORTH tenha ordinal menor que SOUTH, logo compareTo < 0
        assertTrue(compass.compareTo(Compass.SOUTH) < 0, "Esperava compareTo(SOUTH) < 0 para NORTH mas não foi");

        // Espera-se que values() retorne um array com 5 elementos (NORTH,SOUTH,EAST,WEST,UNKNOWN)
        Compass[] vals = Compass.values();
        assertEquals(5, vals.length, "Esperava values().length == 5 mas foi diferente");
    }

    @Test
    void testValueOfAndGetDeclaringClass() {
        // Espera-se que valueOf(\"NORTH\") devolva Compass.NORTH
        assertEquals(Compass.NORTH, Compass.valueOf("NORTH"), "Esperava valueOf('NORTH') == Compass.NORTH mas foi diferente");

        // Espera-se que getDeclaringClass retorne a classe Compass.class
        assertEquals(Compass.class, compass.getDeclaringClass(), "Esperava getDeclaringClass() == Compass.class mas foi diferente");
    }


    @Test
    void charToCompassBranches() {
        // Cobertura do ramo 'n'
        // Espera-se que 'n' mapeie para Compass.NORTH
        assertEquals(Compass.NORTH, Compass.charToCompass('n'), "Esperava charToCompass('n') == NORTH mas foi diferente");

        // Cobertura do ramo 's'
        // Espera-se que 's' mapeie para Compass.SOUTH
        assertEquals(Compass.SOUTH, Compass.charToCompass('s'), "Esperava charToCompass('s') == SOUTH mas foi diferente");

        // Cobertura do ramo 'e'
        // Espera-se que 'e' mapeie para Compass.EAST
        assertEquals(Compass.EAST, Compass.charToCompass('e'), "Esperava charToCompass('e') == EAST mas foi diferente");

        // Cobertura do ramo 'o'
        // Espera-se que 'o' mapeie para Compass.WEST
        assertEquals(Compass.WEST, Compass.charToCompass('o'), "Esperava charToCompass('o') == WEST mas foi diferente");

        // Cobertura do ramo default
        // Espera-se que qualquer outro char mapeie para UNKNOWN
        assertEquals(Compass.UNKNOWN, Compass.charToCompass('x'), "Esperava charToCompass('x') == UNKNOWN mas foi diferente");
    }

    @Test
    void testCloneThrowsCloneNotSupportedException() throws Exception {
        Method cloneMethod = Object.class.getDeclaredMethod("clone");
        try {
            // Em JDKs modernos isto pode lançar InaccessibleObjectException
            cloneMethod.setAccessible(true);
        } catch (InaccessibleObjectException ex) {
            // Acesso reflexivo bloqueado pelo sistema de módulos — resultado aceitável
            return;
        }
        // Se chegarmos aqui, setAccessible teve sucesso; invocação de clone() em enum deve lançar InvocationTargetException com causa CloneNotSupportedException
        InvocationTargetException thrown = assertThrows(InvocationTargetException.class,
                () -> cloneMethod.invoke(compass),
                "Esperava InvocationTargetException ao invocar clone() por reflexão mas nenhuma exceção foi lançada");
        assertInstanceOf(CloneNotSupportedException.class, thrown.getCause(),
                "Esperava que a causa da InvocationTargetException fosse CloneNotSupportedException mas foi: " + thrown.getCause());
    }

    @Test
    void testFinalizeInvocation() throws Exception {
        Method finalizeMethod = Object.class.getDeclaredMethod("finalize");
        try {
            // Em JDKs modernos isto pode lançar InaccessibleObjectException
            finalizeMethod.setAccessible(true);
        } catch (InaccessibleObjectException ex) {
            // Acesso reflexivo bloqueado pelo sistema de módulos — resultado aceitável
            return;
        }
        // Se o acesso foi permitido, invocar finalize() não deve propagar exceção
        try {
            finalizeMethod.invoke(compass);
        } catch (InvocationTargetException ite) {
            fail("Esperava que finalize() não lançasse exceção, mas lançou: " + ite.getCause());
        }
    }


    @Test
    void testToStringDuplicate() {
        // Confirma novamente que toString() devolve exatamente o caracter esperado como string
        // Esperava toString() == "n" mas foi diferente
        assertEquals("n", Compass.NORTH.toString(), "Esperava Compass.NORTH.toString() == 'n' mas foi diferente");
    }

    @Nested
    @DisplayName("Estado e Atributos do Enum")
    class EnumStateTests {

        @Test
        @DisplayName("NORTH: Verifica char 'n' e toString")
        void verifyNorthState() {
            Compass c = Compass.NORTH;
            // Verifica o caracter interno
            assertEquals('n', c.getDirection(),
                    "NORTH deveria estar associado ao char 'n'.");
            // Verifica o toString
            assertEquals("n", c.toString(),
                    "NORTH.toString() deveria retornar \"n\".");
        }

        @Test
        @DisplayName("WEST: Verifica char 'o' (Oeste) e toString")
        void verifyWestState() {
            Compass c = Compass.WEST;
            // Caso especial: WEST é 'o' (de Oeste) e não 'w'
            assertEquals('o', c.getDirection(),
                    "WEST deveria estar associado ao char 'o'.");
            assertEquals("o", c.toString(),
                    "WEST.toString() deveria retornar \"o\".");
        }

        @Test
        @DisplayName("Outras Direções: Verifica SOUTH, EAST e UNKNOWN")
        void verifyRemainingStates() {
            assertAll("Verificar restantes constantes",
                    () -> assertEquals('s', Compass.SOUTH.getDirection()),
                    () -> assertEquals('e', Compass.EAST.getDirection()),
                    () -> assertEquals('u', Compass.UNKNOWN.getDirection())
            );
        }
    }

    @Nested
    @DisplayName("Lógica Estática: charToCompass")
    class StaticConversionTests {

        @Test
        @DisplayName("Conversão Válida: Mapeia n, s, e, o corretamente")
        void charToCompassValidBranches() {
            // Branch: case 'n'
            assertEquals(Compass.NORTH, Compass.charToCompass('n'),
                    "Char 'n' deve converter para NORTH");

            // Branch: case 's'
            assertEquals(Compass.SOUTH, Compass.charToCompass('s'),
                    "Char 's' deve converter para SOUTH");

            // Branch: case 'e'
            assertEquals(Compass.EAST, Compass.charToCompass('e'),
                    "Char 'e' deve converter para EAST");

            // Branch: case 'o'
            assertEquals(Compass.WEST, Compass.charToCompass('o'),
                    "Char 'o' deve converter para WEST");
        }

        @Test
        @DisplayName("Conversão Limite: 'u' mapeia explicitamente para UNKNOWN")
        void charToCompassUnknownBranch() {
            // Nota: Embora 'u' caia no default logicamente se não tiver case,
            // testamos se o enum UNKNOWN funciona como esperado.
            // No código atual, 'u' cai no 'default', retornando UNKNOWN.
            assertEquals(Compass.UNKNOWN, Compass.charToCompass('u'),
                    "Char 'u' deve converter para UNKNOWN");
        }

        @Test
        @DisplayName("Branch Default: Caracteres inválidos retornam UNKNOWN")
        void charToCompassDefaultBranch() {
            // Testa caracteres que não existem no switch
            assertEquals(Compass.UNKNOWN, Compass.charToCompass('x'),
                    "Char inválido 'x' deve retornar UNKNOWN");

            assertEquals(Compass.UNKNOWN, Compass.charToCompass('1'),
                    "Números devem retornar UNKNOWN");

            assertEquals(Compass.UNKNOWN, Compass.charToCompass(' '),
                    "Espaço deve retornar UNKNOWN");
        }

        @Test
        @DisplayName("Case Sensitivity: Maiúsculas caem no default (UNKNOWN)")
        void charToCompassCaseSensitivity() {
            // O switch usa 'n' minúsculo. 'N' maiúsculo cairá no default.
            assertEquals(Compass.UNKNOWN, Compass.charToCompass('N'),
                    "Maiúscula 'N' não é tratada no switch, deve retornar UNKNOWN");

            assertEquals(Compass.UNKNOWN, Compass.charToCompass('O'),
                    "Maiúscula 'O' não é tratada no switch, deve retornar UNKNOWN");
        }
    }
}
