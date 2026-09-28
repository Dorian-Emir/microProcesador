package procesador;

import almacenamiento.MemoriaRAM;
import procesador.ALU.OperacionMIPS;

public class UnidadControl {
    
    private ALU alu;
    private BancoRegistros registros;
    private ContadorPrograma pc;
    private MemoriaRAM ram;

    // Constantes para simular opcodes básicos de MIPS
    private final int OPCODE_R_TYPE = 0; // Instrucciones de registro a registro (add, sub, mul, slt)
    private final int OPCODE_ADDI = 8;   // Suma inmediata
    private final int OPCODE_BEQ = 4;    // Branch on equal (Control de flujo)
    private final int OPCODE_J = 2;      // Jump incondicional (Control de flujo)

    public UnidadControl(ALU alu, BancoRegistros registros, ContadorPrograma pc, MemoriaRAM ram) {
        this.alu = alu;
        this.registros = registros;
        this.pc = pc;
        this.ram = ram;
    }

    /**
     * Extrae la instrucción, la decodifica y ejecuta el ciclo.
     */
    public void ejecutarCiclo() {
        // 1. FETCH (Búsqueda): Obtener la instrucción de la RAM usando el PC
        int instruccion = ram.leer(pc.getDireccion());
        pc.incrementar(); // El PC siempre avanza al iniciar el ciclo

        // 2. DECODE (Decodificación): Extraer el opcode (los 6 bits más significativos en MIPS real, 
        // pero para el simulador podemos simplificar la abstracción).
        // En una implementación de bits, sería algo como: int opcode = (instruccion >>> 26) & 0x3F;
        // Para simplificar la emulación educativa, asumamos que tu instrucción guarda el opcode.
        int opcode = decodificarOpcode(instruccion);

        // 3. EXECUTE (Ejecución):
        switch (opcode) {
            case OPCODE_R_TYPE:
                ejecutarTipoR(instruccion);
                break;
            case OPCODE_ADDI:
                ejecutarAddi(instruccion);
                break;
            case OPCODE_BEQ:
                ejecutarBeq(instruccion);
                break;
            case OPCODE_J:
                ejecutarJump(instruccion);
                break;
            default:
                throw new UnsupportedOperationException("Instrucción no soportada por el simulador.");
        }
    }
    
    

 // Método real para ejecutar instrucciones Tipo R (como la suma)
    private void ejecutarTipoR(int instruccion) {
        // 1. Extraer los segmentos de la instrucción usando máscaras de bits MIPS
        int rs = (instruccion >>> 21) & 0x1F;    // 5 bits para Registro Origen 1
        int rt = (instruccion >>> 16) & 0x1F;    // 5 bits para Registro Origen 2
        int rd = (instruccion >>> 11) & 0x1F;    // 5 bits para Registro Destino
        int funct = instruccion & 0x3F;          // Últimos 6 bits que indican la operación matemática
        
        // 2. Leer los valores físicos del Banco de Registros
        int valorRs = registros.leerRegistro(rs);
        int valorRt = registros.leerRegistro(rt);
        int resultado = 0;

        // 3. Ejecutar en la ALU dependiendo del código 'funct'
        // El 'funct' binario 100000 equivale a 32 en decimal (Suma en MIPS)
        if (funct == 32) { 
            resultado = alu.ejecutarOperacion(valorRs, valorRt, OperacionMIPS.SUMA);
        } 
        // Si quisieras agregar la multiplicación (funct 24) o resta (funct 34), agregarías más "else if" aquí.

        // 4. Escribir el resultado de la ALU de vuelta en el registro destino
        registros.escribirRegistro(rd, resultado);
    }

    private void ejecutarAddi(int instruccion) {
        // Extrae rs, rt y el valor inmediato.
        // valorRt = rs + inmediato
    }

    private void ejecutarBeq(int instruccion) {
        // Extrae rs, rt y el offset (salto).
        // Resta rs y rt en la ALU. Si alu.getBanderaZero() es true, modifica el PC (Control de flujo).
    }

    private void ejecutarJump(int instruccion) {
        // Extrae la dirección destino y fuerza al PC a ir allí (pc.setDireccion(...))
    }

 // Método real para decodificar (extrae los 6 bits más significativos)
    private int decodificarOpcode(int instruccion) {
        // Desplaza 26 bits a la derecha y aplica una máscara para asegurar que sean solo 6 bits
        return (instruccion >>> 26) & 0x3F; 
    }
}