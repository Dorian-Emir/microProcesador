package almacenamiento;

public abstract class UnidadAlmacenamiento {
    protected int anchoPalabra;
    protected int[] celdas;

    public UnidadAlmacenamiento(int cantidadCeldas) {
        // La práctica exige ajustar el ancho de palabra a 32 bits
        this.anchoPalabra = 32; 
        this.celdas = new int[cantidadCeldas];
    }

    public int leer(int direccion) {
        if (direccion >= 0 && direccion < celdas.length) {
            return celdas[direccion];
        }
        throw new IndexOutOfBoundsException("Fallo de hardware: Dirección inválida.");
    }

    public void escribir(int direccion, int dato) {
        if (direccion >= 0 && direccion < celdas.length) {
            celdas[direccion] = dato;
        } else {
            throw new IndexOutOfBoundsException("Fallo de hardware: Dirección inválida.");
        }
    }
}