package Pruebas;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import procesador.BancoRegistros;

public class BancoRegistrosTest {

    private BancoRegistros registros;

    @BeforeEach
    public void setUp() {
        registros = new BancoRegistros();
    }

    @Test
    public void testRegistroCeroInmutable() {
        registros.escribirRegistro(0, 999);
        assertEquals(0, registros.leerRegistro(0), "El registro $0 siempre debe valer 0 en MIPS");
    }

    @Test
    public void testLecturaEscrituraNormal() {
        registros.escribirRegistro(5, 42);
        assertEquals(42, registros.leerRegistro(5), "El registro $5 debe almacenar el valor 42");
    }
}