package procesador;

public class BancoRegistros {
    private int[] registros;

    public BancoRegistros() {
        // MIPS tiene 32 registros de 32 bits
        registros = new int[32]; 
        // El registro 0 siempre es constante 0
        registros[0] = 0; 
    }

    public int leerRegistro(int numeroRegistro) {
        if (numeroRegistro >= 0 && numeroRegistro < 32) {
            return registros[numeroRegistro];
        }
        throw new IllegalArgumentException("Registro inexistente.");
    }

    public void escribirRegistro(int numeroRegistro, int dato) {
        // Se bloquea la escritura en el registro 0, los demás son modificables
        if (numeroRegistro > 0 && numeroRegistro < 32) {
            registros[numeroRegistro] = dato;
        }
    }
    
    public void imprimirEstado() {
        System.out.print("   [ESTADO REGISTROS] ");
        for (int i = 0; i < 32; i++) {
            if (registros[i] != 0) {
                System.out.print("$" + i + " = " + registros[i] + " | ");
            }
        }
        System.out.println();
    }
    
}