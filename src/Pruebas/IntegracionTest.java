package Pruebas;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import almacenamiento.MemoriaRAM;
import procesador.ALU;
import procesador.BancoRegistros;
import procesador.ContadorPrograma;
import procesador.UnidadControl;

public class IntegracionTest {

    private MemoriaRAM ram;
    private BancoRegistros registros;
    private ContadorPrograma pc;
    private ALU alu;
    private UnidadControl cpu;

    @BeforeEach
    public void setUp() {
        ram = new MemoriaRAM();
        registros = new BancoRegistros();
        pc = new ContadorPrograma();
        alu = new ALU();
        cpu = new UnidadControl(alu, registros, pc, ram);
    }

    @Test
    public void testCicloEjecucionSuma() {
        // 1. Preparar el estado inicial
        registros.escribirRegistro(8, 100); // $8 = 100
        registros.escribirRegistro(9, 50);  // $9 = 50
        
        // 2. Quemar instrucción MIPS en RAM: add $10, $8, $9 (Opcode 0, rs 8, rt 9, rd 10, funct 32)
        int instruccionAdd = 0b000000_01000_01001_01010_00000_100000;
        ram.escribir(0, instruccionAdd);
        
        // 3. Ejecutar un ciclo completo
        cpu.ejecutarCiclo();
        
        // 4. Verificar la integración
        assertEquals(150, registros.leerRegistro(10), "El registro destino $10 debe tener la suma (150)");
        assertEquals(1, pc.getDireccion(), "El Contador de Programa debió incrementarse a 1");
    }
}