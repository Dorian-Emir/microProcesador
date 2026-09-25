package procesador;

public class ContadorPrograma {
    
    // Almacena la dirección de 32 bits de la siguiente instrucción a ejecutar
    private int direccionActual;
    
    // Define el avance del PC. Un valor de 1 avanza una celda en el arreglo de memoria.
    private int salto;

    public ContadorPrograma() {
        this.direccionActual = 0;
        this.salto = 1; 
    }

    /**
     * Obtiene la dirección actual a la que apunta el PC.
     */
    public int getDireccion() {
        return direccionActual;
    }

    /**
     * Permite forzar un salto a una dirección específica (esencial para instrucciones Jump o Branch).
     */
    public void setDireccion(int nuevaDireccion) {
        if (nuevaDireccion >= 0) {
            this.direccionActual = nuevaDireccion;
        } else {
            throw new IllegalArgumentException("Error: La dirección del PC no puede ser negativa.");
        }
    }

    /**
     * Incrementa el PC durante la búsqueda de instrucciones (fetch) para apuntar a la siguiente instrucción.
     */
    public void incrementar() {
        this.direccionActual += salto;
    }
    
    /**
     * Reinicia el PC a 0.
     */
    public void reset() {
        this.direccionActual = 0;
    }
}