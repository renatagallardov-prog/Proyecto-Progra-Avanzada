package com.parquetematico.gestion;

import com.parquetematico.modelo.Atraccion;
import com.parquetematico.modelo.EstadoReserva;
import com.parquetematico.modelo.Grupo;
import com.parquetematico.modelo.Reserva;
import com.parquetematico.modelo.TipoAtraccion;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * SIA-3: Carga datos iniciales de ejemplo que permiten ejecutar y probar
 * cualquiera de las funcionalidades del sistema sin partir de cero.
 * Se usa únicamente si no existen datos guardados previamente (primera ejecución).
 */
public class DatosIniciales {

    public static void cargar(GestorParque gestor) {
        Atraccion montania = new Atraccion("A01", "Montaña Rusa Fénix", TipoAtraccion.EXTREMA,
                24, LocalTime.of(10, 0), LocalTime.of(18, 0), true);
        Atraccion rio = new Atraccion("A02", "Río Rápido", TipoAtraccion.ACUATICA,
                16, LocalTime.of(10, 0), LocalTime.of(19, 0), true);
        Atraccion carrusel = new Atraccion("A03", "Carrusel Encantado", TipoAtraccion.INFANTIL,
                30, LocalTime.of(10, 0), LocalTime.of(20, 0), true);
        Atraccion espectaculo = new Atraccion("A04", "Show Nocturno de Luces", TipoAtraccion.ESPECTACULO,
                200, LocalTime.of(20, 0), LocalTime.of(21, 0), true);

        gestor.agregarAtraccion(montania);
        gestor.agregarAtraccion(rio);
        gestor.agregarAtraccion(carrusel);
        gestor.agregarAtraccion(espectaculo);

        Grupo g1 = new Grupo("11111111-1", "Colegio San Andrés", "contacto@sanandres.cl", 20);
        Grupo g2 = new Grupo("22222222-2", "Familia Pérez", "perez@correo.cl", 4);
        Grupo g3 = new Grupo("33333333-3", "Empresa TechCorp", "eventos@techcorp.cl", 15);

        LocalDate manana = LocalDate.now().plusDays(1);

        montania.getReservas().add(new Reserva("R001", "A01", g1, manana,
                LocalTime.of(11, 0), 20, EstadoReserva.CONFIRMADA));
        rio.getReservas().add(new Reserva("R002", "A02", g2, manana,
                LocalTime.of(12, 30), 4, EstadoReserva.PENDIENTE));
        espectaculo.getReservas().add(new Reserva("R003", "A04", g3, manana,
                LocalTime.of(20, 0), 15, EstadoReserva.CONFIRMADA));
    }
}
