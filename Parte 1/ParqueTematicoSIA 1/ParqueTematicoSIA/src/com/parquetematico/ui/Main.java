package com.parquetematico.ui;

import com.parquetematico.controlador.ParqueController;

import javax.swing.SwingUtilities;
import java.util.Scanner;

/**
 * Punto de entrada del programa.
 *
 * SIA-O4 (MVC): esta clase arma el Controlador y se lo pasa a la Vista que
 * corresponda (consola o ventana). Ni MenuConsola ni VentanaPrincipal tocan
 * directamente el GestorParque o la persistencia, todo pasa por el
 * ParqueController.
 *
 * SIA-10: se pregunta al usuario si quiere consola o ventana.
 */
public class Main {

    public static void main(String[] args) {

        ParqueController controlador = new ParqueController();
        controlador.iniciar(); // SIA-11: carga los datos guardados (o de ejemplo si es la primera vez)

        Scanner sc = new Scanner(System.in);
        System.out.println("=== Sistema de Administracion - Parque Tematico ===");
        System.out.print("Como desea usar el sistema? (1) Consola  (2) Ventana: ");
        String opcion = sc.nextLine().trim();

        if (opcion.equals("2")) {
            // En modo ventana, guardamos los datos cuando el usuario cierra la ventana.
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new VentanaPrincipal(controlador).setVisible(true);
                }
            });
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    controlador.guardarTodo();
                }
            }));
        } else {
            new MenuConsola(controlador, sc).iniciar();
            controlador.guardarTodo();
            System.out.println("Datos guardados. Hasta pronto!");
        }
    }
}
