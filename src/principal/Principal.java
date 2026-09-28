package principal;
import procesador.*;
import almacenamiento.MemoriaRAM;

public class Principal {


	    public static void main(String[] args) {
	        MemoriaRAM ram = new MemoriaRAM(); 
	        BancoRegistros registros = new BancoRegistros(); 
	        ContadorPrograma pc = new ContadorPrograma(); 
	        ALU alu = new ALU(); 
	        UnidadControl cpu = new UnidadControl(alu, registros, pc, ram);

	        // 1. Preparar el escenario: Cargar valores arbitrarios en los registros
	        registros.escribirRegistro(1, 15); // $1 = 15
	        registros.escribirRegistro(2, 25); // $2 = 25
	        System.out.println("Valor inicial en registro 3: " + registros.leerRegistro(3));

	        // 2. Quemar la instrucción binaria en la RAM (add $3, $1, $2)
	        // Usamos los guiones bajos para separar visualmente Opcode | rs | rt | rd | shamt | funct
	        int instruccionSuma = 0b000000_00001_00010_00011_00000_100000;
	        ram.escribir(0, instruccionSuma); 

	        // 3. Ejecutar el ciclo de reloj
	        cpu.ejecutarCiclo();

	        // 4. Validar el resultado
	        System.out.println("Valor final en registro 3: " + registros.leerRegistro(3));
	    }
    
}