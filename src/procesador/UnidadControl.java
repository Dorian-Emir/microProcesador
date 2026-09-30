package procesador;

import almacenamiento.MemoriaRAM;
import procesador.ALU.OperacionMIPS;

public class UnidadControl {
    
    private ALU alu;
    private BancoRegistros registros;
    private ContadorPrograma pc;
    private MemoriaRAM ram;

    // --- MÁQUINA DE ESTADOS MULTICICLO ---
    public enum Fase { FETCH, DECODE, EXECUTE, MEMORY, WRITE_BACK }
    private Fase faseActual = Fase.FETCH;

    // --- REGISTROS INTERMEDIOS (LATCHES) ---
    private int IR = 0;       // Registro de Instrucción
    private int A = 0;        // Registro temporal para dato rs
    private int B = 0;        // Registro temporal para dato rt
    private int ALUOut = 0;   // Salida temporal de la ALU
    private int MDR = 0;      // Registro de Dato de Memoria

    // --- OPCODES MIPS ---
    private final int OPCODE_R_TYPE = 0x00; 
    private final int OPCODE_J      = 0x02; 
    private final int OPCODE_BEQ    = 0x04; 
    private final int OPCODE_BNE    = 0x05; 
    private final int OPCODE_ADDI   = 0x08; 
    private final int OPCODE_LW     = 0x23; 
    private final int OPCODE_SW     = 0x2B; 

    private final int FUNCT_ADD = 0x20, FUNCT_SUB = 0x22, FUNCT_AND = 0x24;
    private final int FUNCT_OR  = 0x25, FUNCT_SLT = 0x2A, FUNCT_MUL = 0x18; 

    public UnidadControl(ALU alu, BancoRegistros registros, ContadorPrograma pc, MemoriaRAM ram) {
        this.alu = alu;
        this.registros = registros;
        this.pc = pc;
        this.ram = ram;
    }

    public Fase getFaseActual() {
        return faseActual;
    }

    // Avanza una sola fase del reloj por cada llamada
    public String ejecutarCicloReloj() {
        String logAccion = "";

        switch (faseActual) {
            case FETCH:
                IR = ram.leer(pc.getDireccion());
                if (IR == 0) throw new RuntimeException("HALT: Fin del programa. Memoria vacía.");
                logAccion = "[FETCH] Extrayendo instrucción de RAM. PC avanza a " + (pc.getDireccion() + 1);
                pc.incrementar();
                faseActual = Fase.DECODE;
                break;

            case DECODE:
                int rs = (IR >>> 21) & 0x1F;
                int rt = (IR >>> 16) & 0x1F;
                A = registros.leerRegistro(rs);
                B = registros.leerRegistro(rt);
                logAccion = "[DECODE] Decodificando. Valores leídos -> rs: " + A + ", rt: " + B;
                faseActual = Fase.EXECUTE;
                break;

            case EXECUTE:
                int opcode = (IR >>> 26) & 0x3F;
                if (opcode == OPCODE_R_TYPE) {
                    int funct = IR & 0x3F;
                    ejecutarALUTipoR(funct);
                    logAccion = "[EXECUTE] Operación Tipo R calculada en ALU = " + ALUOut;
                    faseActual = Fase.WRITE_BACK; // R-Type salta a Write-Back
                } 
                else if (opcode == OPCODE_ADDI) {
                    short inmediato = (short) (IR & 0xFFFF);
                    ALUOut = alu.ejecutarOperacion(A, inmediato, OperacionMIPS.SUMA);
                    logAccion = "[EXECUTE] Suma Inmediata (ADDI) calculada = " + ALUOut;
                    faseActual = Fase.WRITE_BACK;
                } 
                else if (opcode == OPCODE_LW || opcode == OPCODE_SW) {
                    short offset = (short) (IR & 0xFFFF);
                    ALUOut = A + offset; // Calcular dirección de memoria
                    logAccion = "[EXECUTE] Dirección de RAM calculada = " + ALUOut;
                    faseActual = Fase.MEMORY; // LW y SW necesitan acceder a memoria
                } 
                else if (opcode == OPCODE_BEQ) {
                    short offset = (short) (IR & 0xFFFF);
                    alu.ejecutarOperacion(A, B, OperacionMIPS.RESTA);
                    if (alu.getBanderaZero()) pc.setDireccion(pc.getDireccion() + offset - 1);
                    logAccion = "[EXECUTE] BEQ evaluado. Bandera Zero: " + alu.getBanderaZero();
                    faseActual = Fase.FETCH; // Branch termina aquí
                }
                else if (opcode == OPCODE_BNE) {
                    short offset = (short) (IR & 0xFFFF);
                    alu.ejecutarOperacion(A, B, OperacionMIPS.RESTA);
                    if (!alu.getBanderaZero()) pc.setDireccion(pc.getDireccion() + offset - 1);
                    logAccion = "[EXECUTE] BNE evaluado. Bandera Zero: " + alu.getBanderaZero();
                    faseActual = Fase.FETCH; // Branch termina aquí
                }
                else if (opcode == OPCODE_J) {
                    int jumpAddr = IR & 0x3FFFFFF;
                    pc.setDireccion(jumpAddr);
                    logAccion = "[EXECUTE] Salto Incondicional (J) a PC = " + jumpAddr;
                    faseActual = Fase.FETCH; // Jump termina aquí
                }
                break;

            case MEMORY:
                int opMem = (IR >>> 26) & 0x3F;
                if (opMem == OPCODE_LW) {
                    MDR = ram.leer(ALUOut);
                    logAccion = "[MEMORY] Dato leído de RAM[" + ALUOut + "] = " + MDR;
                    faseActual = Fase.WRITE_BACK;
                } 
                else if (opMem == OPCODE_SW) {
                    ram.escribir(ALUOut, B);
                    logAccion = "[MEMORY] Dato " + B + " escrito en RAM[" + ALUOut + "]";
                    faseActual = Fase.FETCH; // Store Word termina aquí
                }
                break;

            case WRITE_BACK:
                int opWb = (IR >>> 26) & 0x3F;
                if (opWb == OPCODE_R_TYPE) {
                    int rd = (IR >>> 11) & 0x1F;
                    registros.escribirRegistro(rd, ALUOut);
                    logAccion = "[WRITE_BACK] Resultado " + ALUOut + " guardado en $" + rd;
                } 
                else if (opWb == OPCODE_ADDI) {
                    int rtAddi = (IR >>> 16) & 0x1F;
                    registros.escribirRegistro(rtAddi, ALUOut);
                    logAccion = "[WRITE_BACK] Resultado " + ALUOut + " guardado en $" + rtAddi;
                } 
                else if (opWb == OPCODE_LW) {
                    int rtLw = (IR >>> 16) & 0x1F;
                    registros.escribirRegistro(rtLw, MDR);
                    logAccion = "[WRITE_BACK] Dato " + MDR + " (desde RAM) guardado en $" + rtLw;
                }
                logAccion += " \n----------------------------------";
                faseActual = Fase.FETCH; // Reiniciar el ciclo para la siguiente instrucción
                break;
        }
        return logAccion;
    }

    private void ejecutarALUTipoR(int funct) {
        switch (funct) {
            case FUNCT_ADD: ALUOut = alu.ejecutarOperacion(A, B, OperacionMIPS.SUMA); break;
            case FUNCT_SUB: ALUOut = alu.ejecutarOperacion(A, B, OperacionMIPS.RESTA); break;
            case FUNCT_MUL: ALUOut = alu.ejecutarOperacion(A, B, OperacionMIPS.MULTIPLICACION); break;
            case FUNCT_AND: ALUOut = alu.ejecutarOperacion(A, B, OperacionMIPS.AND); break;
            case FUNCT_OR:  ALUOut = alu.ejecutarOperacion(A, B, OperacionMIPS.OR); break;
            case FUNCT_SLT: ALUOut = alu.ejecutarOperacion(A, B, OperacionMIPS.SET_LESS_THAN); break;
        }
    }
}