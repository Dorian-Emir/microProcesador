package procesador;

public class ALU {
    
    private boolean banderaZero;
    private int resultadoActual;

    /**
     * Definimos los códigos de operación que la ALU puede ejecutar.
     * Estos cubrirán los cálculos necesarios para las pruebas de usuario.
     */
    public enum OperacionMIPS {
        SUMA, RESTA, MULTIPLICACION, AND, OR, SET_LESS_THAN
    }

    public ALU() {
        this.banderaZero = false;
        this.resultadoActual = 0;
    }

    /**
     * Ejecuta una operación aritmética o lógica entre dos operandos de 32 bits.
     * 
     * @param operando1 El primer valor (ej. contenido del registro rs)
     * @param operando2 El segundo valor (ej. contenido del registro rt o un inmediato)
     * @param operacion El código de la operación a ejecutar
     * @return El resultado del cálculo de 32 bits
     */
    public int ejecutarOperacion(int operando1, int operando2, OperacionMIPS operacion) {
        switch (operacion) {
            case SUMA:
                resultadoActual = operando1 + operando2;
                break;
            case RESTA:
                resultadoActual = operando1 - operando2;
                break;
            case MULTIPLICACION:
                resultadoActual = operando1 * operando2;
                break;
            case AND:
                resultadoActual = operando1 & operando2;
                break;
            case OR:
                resultadoActual = operando1 | operando2;
                break;
            case SET_LESS_THAN: // Simula la instrucción 'slt' de MIPS
                resultadoActual = (operando1 < operando2) ? 1 : 0;
                break;
            default:
                throw new IllegalArgumentException("Error de hardware: Operación no soportada por la ALU.");
        }
        
        // Actualiza la Bandera Zero: es true si el resultado es exactamente 0
        banderaZero = (resultadoActual == 0);
        
        return resultadoActual;
    }

    /**
     * Devuelve el estado de la bandera Zero.
     * Fundamental para las instrucciones de control de flujo como BEQ (Branch if Equal).
     */
    public boolean getBanderaZero() {
        return banderaZero;
    }
}