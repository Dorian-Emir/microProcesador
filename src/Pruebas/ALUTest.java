package Pruebas;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import procesador.ALU;
import procesador.ALU.OperacionMIPS;

public class ALUTest {

    private ALU alu;

    @BeforeEach
    public void setUp() {
        // Se ejecuta antes de cada @Test para inicializar una ALU limpia
        alu = new ALU();
    }

    @Test
    public void testSumaCorrecta() {
        int resultado = alu.ejecutarOperacion(15, 25, OperacionMIPS.SUMA);
        assertEquals(40, resultado, "La ALU debe sumar correctamente 15 + 25");
        assertFalse(alu.getBanderaZero(), "La bandera Zero debe ser false porque el resultado no es 0");
    }

    @Test
    public void testBanderaZeroActivada() {
        // Esto es vital para las instrucciones de control de flujo (beq) del MIPS
        int resultado = alu.ejecutarOperacion(10, 10, OperacionMIPS.RESTA);
        assertEquals(0, resultado, "La resta de números iguales debe dar 0");
        assertTrue(alu.getBanderaZero(), "La bandera Zero debe ser true cuando el resultado es 0");
    }

    @Test
    public void testOperacionNoSoportada() {
        // Evaluamos si el sistema detona correctamente la excepción de hardware
        Exception excepcion = assertThrows(NullPointerException.class, () -> {
            alu.ejecutarOperacion(5, 5, null);
        });
        assertNotNull(excepcion, "Debe lanzar una excepción si la operación no existe");
    }
}