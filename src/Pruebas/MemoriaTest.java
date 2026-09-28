package Pruebas;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import almacenamiento.MemoriaRAM;
import almacenamiento.MemoriaROM;

public class MemoriaTest {

    @Test
    public void testRAMEscrituraLectura() {
        MemoriaRAM ram = new MemoriaRAM();
        ram.escribir(10, 2048);
        assertEquals(2048, ram.leer(10), "La RAM debe devolver el dato escrito en la dirección 10");
    }

    @Test
    public void testROMBloqueaEscritura() {
        MemoriaROM rom = new MemoriaROM();
        Exception excepcion = assertThrows(UnsupportedOperationException.class, () -> {
            rom.escribir(5, 100);
        });
        assertNotNull(excepcion, "La ROM debe lanzar excepción al intentar escribir en tiempo de ejecución");
    }
}