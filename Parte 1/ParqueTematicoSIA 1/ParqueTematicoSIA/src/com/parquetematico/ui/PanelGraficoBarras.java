package com.parquetematico.ui;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SIA-O1: componente grafico estadistico.
 *
 * Es un JPanel que yo mismo dibujo con Graphics2D (no use ninguna libreria
 * externa de graficos). Recibe un mapa de "nombre de atraccion -> porcentaje
 * de ocupacion" y dibuja una barra por cada una. El color de la barra
 * cambia segun que tan llena esta la atraccion, para que se entienda de un
 * vistazo (verde = tranquilo, naranja = ocupado, rojo = casi lleno o lleno).
 */
public class PanelGraficoBarras extends JPanel {

    private Map<String, Double> datos = new LinkedHashMap<>();

    public void actualizarDatos(Map<String, Double> nuevosDatos) {
        this.datos = nuevosDatos;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int ancho = getWidth();
        int alto = getHeight();
        int margenIzquierdo = 40;
        int margenInferior = 60;
        int margenSuperior = 20;

        // Ejes
        g2.setColor(Color.GRAY);
        g2.drawLine(margenIzquierdo, margenSuperior, margenIzquierdo, alto - margenInferior);
        g2.drawLine(margenIzquierdo, alto - margenInferior, ancho - 10, alto - margenInferior);

        if (datos.isEmpty()) {
            g2.drawString("No hay datos para graficar en este rango de fechas.", margenIzquierdo + 10, alto / 2);
            return;
        }

        int cantidadBarras = datos.size();
        int anchoDisponible = ancho - margenIzquierdo - 30;
        int anchoBarra = anchoDisponible / cantidadBarras;
        int altoMaximo = alto - margenSuperior - margenInferior;

        // Referencia del 100% para que las barras tengan una escala fija
        double escalaMaxima = 100.0;

        int i = 0;
        for (Map.Entry<String, Double> entrada : datos.entrySet()) {
            double porcentaje = entrada.getValue();
            if (porcentaje > escalaMaxima) {
                escalaMaxima = porcentaje; // por si alguna atraccion esta sobre el 100%
            }
            i++;
        }

        i = 0;
        for (Map.Entry<String, Double> entrada : datos.entrySet()) {
            String nombre = entrada.getKey();
            double porcentaje = entrada.getValue();

            int alturaBarra = (int) ((porcentaje / escalaMaxima) * altoMaximo);
            int x = margenIzquierdo + (i * anchoBarra) + 10;
            int y = alto - margenInferior - alturaBarra;

            g2.setColor(colorSegunOcupacion(porcentaje));
            g2.fillRect(x, y, anchoBarra - 20, alturaBarra);
            g2.setColor(Color.DARK_GRAY);
            g2.drawRect(x, y, anchoBarra - 20, alturaBarra);

            // Porcentaje arriba de la barra
            String textoPorcentaje = String.format("%.0f%%", porcentaje);
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2.drawString(textoPorcentaje, x, y - 5);

            // Nombre de la atraccion abajo, achicado para que quepa
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            String nombreCorto = nombre.length() > 12 ? nombre.substring(0, 12) + "." : nombre;
            FontMetrics fm = g2.getFontMetrics();
            int anchoTexto = fm.stringWidth(nombreCorto);
            g2.drawString(nombreCorto, x + (anchoBarra - 20 - anchoTexto) / 2, alto - margenInferior + 15);

            i++;
        }
    }

    /**
     * Verde si va tranquilo, naranjo si se esta llenando, rojo si esta cerca
     * del limite o se paso. Son valores simples, no hace falta que sean
     * exactos, es solo una ayuda visual rapida (por eso el proyecto pide
     * "monitoreo de capacidad").
     */
    private Color colorSegunOcupacion(double porcentaje) {
        if (porcentaje < 50) {
            return new Color(76, 175, 80); // verde
        } else if (porcentaje < 85) {
            return new Color(255, 152, 0); // naranjo
        } else {
            return new Color(211, 47, 47); // rojo
        }
    }
}
