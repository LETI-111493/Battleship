package iscteiul.ista.battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes unitários para Compass (\"construtor\" e getSize)")
class CompassTest {

    @Nested
    @DisplayName("Testes do \"construtor\" de Compass")
    class ConstructorTests {

        @Test
        @DisplayName("name, ordinal e valueOf simulam comportamento do construtor")
        void nameOrdinalValueOf() {
            assertEquals("NORTH", Compass.NORTH.name(), "Esperava name() == 'NORTH'");
            assertEquals(0, Compass.NORTH.ordinal(), "Esperava ordinal() == 0 para NORTH");
            assertEquals(Compass.NORTH, Compass.valueOf("NORTH"), "Esperava valueOf('NORTH') == NORTH");
        }

        @Test
        @DisplayName("toString e getDirection devolvem o carácter associado")
        void toStringAndGetDirection() {
            assertEquals("n", Compass.NORTH.toString(), "Esperava toString() == 'n' para NORTH");
            assertEquals('n', Compass.NORTH.getDirection(), "Esperava getDirection() == 'n' para NORTH");
        }

        @Test
        @DisplayName("charToCompass cobre todos os ramos do switch")
        void charToCompassBranches() {
            assertEquals(Compass.NORTH, Compass.charToCompass('n'));
            assertEquals(Compass.SOUTH, Compass.charToCompass('s'));
            assertEquals(Compass.EAST, Compass.charToCompass('e'));
            assertEquals(Compass.WEST, Compass.charToCompass('o'));
            assertEquals(Compass.UNKNOWN, Compass.charToCompass('x'), "Esperava ramo default para char não reconhecido");
        }
    }

    @Test
    @DisplayName("getSize\\(values\\) devolve número esperado de constantes e é consistente")
    void getSize_consistentWithValues() {
        assertEquals(5, Compass.values().length, "Esperava 5 constantes em Compass (NORTH,SOUTH,EAST,WEST,UNKNOWN)");
    }
}
