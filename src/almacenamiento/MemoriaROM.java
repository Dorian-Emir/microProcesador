package almacenamiento;

//La ROM ajusta rígidamente su tamaño a 100 celdas y bloquea la escritura en tiempo de ejecución[cite: 1]
public class MemoriaROM extends UnidadAlmacenamiento {
 public MemoriaROM() {
     super(100);
 }

 @Override
 public void escribir(int direccion, int dato) {
     throw new UnsupportedOperationException("Error de hardware: ROM es de solo lectura.");
 }
}