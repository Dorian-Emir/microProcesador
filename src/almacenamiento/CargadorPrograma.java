package almacenamiento;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CargadorPrograma {

    /**
     * Lee un archivo de texto con instrucciones en binario y las carga en la RAM.
     * @param rutaArchivo La ruta del archivo .txt (ej. "script.txt")
     * @param ram La instancia de la MemoriaRAM del simulador
     */
    public static void cargarDesdeArchivo(String rutaArchivo, MemoriaRAM ram) {
        int direccionActual = 0;

        try (BufferedReader lector = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            System.out.println("Cargando script desde: " + rutaArchivo);

            while ((linea = lector.readLine()) != null) {
                linea = linea.trim(); // Quitar espacios en blanco a los lados

                // Ignorar líneas vacías o comentarios (que empiecen con # o //)
                if (linea.isEmpty() || linea.startsWith("#") || linea.startsWith("//")) {
                    continue; 
                }

                // Limpiar guiones bajos por si decides usarlos para separar visualmente los bits
                linea = linea.replace("_", "");

                try {
                    // Usamos parseUnsignedInt para evitar errores si el bit más significativo es 1 (números negativos)
                    int instruccion = Integer.parseUnsignedInt(linea, 2);
                    
                    // Escribir en la memoria y avanzar al siguiente índice
                    ram.escribir(direccionActual, instruccion);
                    System.out.println("  -> Memoria[" + direccionActual + "] cargada con éxito.");
                    direccionActual++;
                    
                } catch (NumberFormatException e) {
                    System.err.println("  -> Error de sintaxis en el script. Línea ignorada: " + linea);
                }
            }
            System.out.println("Carga finalizada. " + direccionActual + " instrucciones listas en la RAM.\n");

        } catch (IOException e) {
            System.err.println("Error fatal: No se pudo leer el archivo de script. " + e.getMessage());
        }
    }
}
