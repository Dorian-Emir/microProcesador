package principal;

import almacenamiento.CargadorPrograma;
import almacenamiento.MemoriaRAM;
import procesador.ALU;
import procesador.BancoRegistros;
import procesador.ContadorPrograma;
import procesador.UnidadControl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SimuladorGUI extends JFrame {

    private MemoriaRAM ram;
    private BancoRegistros registros;
    private ContadorPrograma pc;
    private ALU alu;
    private UnidadControl cpu;

    private DefaultTableModel modeloRegistros;
    private DefaultTableModel modeloMemoria;
    private JTextArea consolaVisual;
    private JLabel lblPC;
    private JLabel lblFase;
    private JButton btnStep;
    private JButton btnReset;

    public SimuladorGUI() {
        inicializarHardware();
        configurarVentana();
        inicializarUI();
        actualizarPantalla();
    }

    private void inicializarHardware() {
        ram = new MemoriaRAM();
        registros = new BancoRegistros();
        pc = new ContadorPrograma();
        alu = new ALU();
        cpu = new UnidadControl(alu, registros, pc, ram);
        CargadorPrograma.cargarDesdeArchivo("script.txt", ram);
    }

    private void configurarVentana() {
        setTitle("Emulador MIPS 32-bits (Arquitectura Multiciclo)");
        setSize(1050, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(15, 23, 42)); 
    }

    private void inicializarUI() {
        // --- PANEL SUPERIOR ---
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelTop.setBackground(new Color(30, 41, 59));
        
        btnStep = new JButton("Pulsar Reloj (Tick)");
        btnStep.setBackground(new Color(249, 115, 22)); 
        btnStep.setForeground(Color.WHITE);
        btnStep.setFont(new Font("Arial", Font.BOLD, 14));
        
        btnReset = new JButton("Reiniciar");
        btnReset.setBackground(new Color(225, 29, 72)); // Botón rojo
        btnReset.setForeground(Color.WHITE);
        btnReset.setFont(new Font("Arial", Font.BOLD, 14));
        
        lblPC = new JLabel(" PC: 0 ");
        lblPC.setForeground(new Color(56, 189, 248));
        lblPC.setFont(new Font("Monospaced", Font.BOLD, 16));
        
        lblFase = new JLabel(" Fase: FETCH ");
        lblFase.setForeground(new Color(167, 243, 208));
        lblFase.setFont(new Font("Monospaced", Font.BOLD, 16));

        panelTop.add(btnStep);
        panelTop.add(btnReset);
        panelTop.add(lblPC);
        panelTop.add(lblFase);
        add(panelTop, BorderLayout.NORTH);

        // --- ACCIONES DE LOS BOTONES ---
        btnStep.addActionListener(e -> ejecutarPasoReloj());
        btnReset.addActionListener(e -> reiniciarSistema());

        // --- PANEL CENTRAL: CONSOLA ---
        consolaVisual = new JTextArea();
        consolaVisual.setBackground(new Color(0, 0, 0));
        consolaVisual.setForeground(new Color(74, 222, 128)); 
        consolaVisual.setFont(new Font("Monospaced", Font.PLAIN, 14));
        consolaVisual.setEditable(false);
        consolaVisual.append("Sistema Multiciclo Inicializado. Presiona 'Pulsar Reloj' para avanzar la primera fase.\n\n");
        JScrollPane scrollConsola = new JScrollPane(consolaVisual);
        scrollConsola.setBorder(BorderFactory.createTitledBorder(null, "Log del Sistema Multiciclo", 0, 0, null, Color.WHITE));
        add(scrollConsola, BorderLayout.CENTER);

        // --- PANELES DE TABLAS (Registros y Memoria) ---
        String[] colReg = {"Registro", "Valor"};
        modeloRegistros = new DefaultTableModel(colReg, 0);
        JTable tablaRegistros = new JTable(modeloRegistros);
        formatearTabla(tablaRegistros);
        JScrollPane scrollRegistros = new JScrollPane(tablaRegistros);
        scrollRegistros.setPreferredSize(new Dimension(200, 0));
        scrollRegistros.setBorder(BorderFactory.createTitledBorder(null, "Banco de Registros", 0, 0, null, Color.WHITE));
        add(scrollRegistros, BorderLayout.EAST);

        String[] colMem = {"Dirección", "Instrucción / Dato en RAM"};
        modeloMemoria = new DefaultTableModel(colMem, 0);
        JTable tablaMemoria = new JTable(modeloMemoria);
        formatearTabla(tablaMemoria);
        JScrollPane scrollMemoria = new JScrollPane(tablaMemoria);
        scrollMemoria.setPreferredSize(new Dimension(0, 200));
        scrollMemoria.setBorder(BorderFactory.createTitledBorder(null, "Vista de Memoria", 0, 0, null, Color.WHITE));
        add(scrollMemoria, BorderLayout.SOUTH);
    }

    private void formatearTabla(JTable tabla) {
        tabla.setBackground(new Color(30, 41, 59));
        tabla.setForeground(new Color(167, 243, 208));
        tabla.setFont(new Font("Monospaced", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(new Color(15, 23, 42));
        tabla.getTableHeader().setForeground(Color.WHITE);
    }

    private void ejecutarPasoReloj() {
        try {
            String accion = cpu.ejecutarCicloReloj();
            consolaVisual.append(accion + "\n");
            // Auto-scroll al fondo
            consolaVisual.setCaretPosition(consolaVisual.getDocument().getLength());
            actualizarPantalla();
        } catch (Exception ex) {
            btnStep.setEnabled(false); 
            consolaVisual.append("\n!!! " + ex.getMessage() + " !!!\n");
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Ejecución Terminada", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void reiniciarSistema() {
        inicializarHardware();
        consolaVisual.setText("--- SISTEMA REINICIADO ---\nScript recargado. Registros y Memoria limpios.\n\n");
        btnStep.setEnabled(true);
        actualizarPantalla();
    }

    private void actualizarPantalla() {
        lblPC.setText(" PC: " + pc.getDireccion() + " ");
        lblFase.setText(" Fase Siguiente: " + cpu.getFaseActual().name() + " ");

        modeloRegistros.setRowCount(0);
        for (int i = 0; i < 32; i++) {
            modeloRegistros.addRow(new Object[]{"$" + i, registros.leerRegistro(i)});
        }

        modeloMemoria.setRowCount(0);
        for (int i = 0; i < 30; i++) {
            int valor = ram.leer(i);
            String binario = String.format("%32s", Integer.toBinaryString(valor)).replace(' ', '0');
            modeloMemoria.addRow(new Object[]{i, binario});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimuladorGUI ventana = new SimuladorGUI();
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        });
    }
}