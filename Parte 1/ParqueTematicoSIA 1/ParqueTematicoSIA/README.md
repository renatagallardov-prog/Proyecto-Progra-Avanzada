# Sistema Parque Tematico (SIA) - INF2236

## Como abrir en NetBeans
1. Descomprime el .zip.
2. Abre NetBeans -> File -> Open Project -> selecciona la carpeta `ParqueTematicoSIA`.
3. NetBeans va a regenerar automaticamente `nbproject/build-impl.xml` (es normal, pasa siempre la primera vez que se abre).
4. Click derecho en el proyecto -> Properties -> Sources: confirma que dice JDK 8. Libraries -> Java Platform: confirma que apunta al JDK 8 instalado.
5. Click derecho en el proyecto -> Run (o F6). La clase principal ya esta configurada: `com.parquetematico.ui.Main`.
6. Al ejecutar, la consola pregunta si quieres modo Consola o Ventana.

## Estructura de paquetes (patron MVC - SIA-O4)
- `modelo`: Atraccion, Grupo, Reserva, TipoAtraccion (enum), EstadoReserva (enum). Este es el **Modelo**.
- `gestion`: GestorParque (logica de negocio + la 1ra coleccion, el Map) y DatosIniciales. Tambien parte del **Modelo**.
- `excepciones`: CapacidadExcedidaException, ElementoNoEncontradoException.
- `persistencia`: PersistenciaCSV (carga/guarda batch en `data/*.csv`).
- `controlador`: ParqueController. Es el **Controlador**: la unica clase que las vistas pueden llamar. El internamente decide si le pide algo al GestorParque o a la Persistencia.
- `ui`: Main, MenuConsola, VentanaPrincipal, PanelGraficoBarras. Son la **Vista**.

## Requisitos obligatorios, donde estan
- SIA-4 (2 colecciones, una anidada, al menos un Map): `GestorParque` tiene `Map<String, Atraccion>` (1ra coleccion). Cada `Atraccion` tiene un `List<Reserva>` adentro (2da coleccion, anidada).
- SIA-5 (sobrecarga en 2 clases): `GestorParque.buscarAtraccion(String)` / `buscarAtraccion(TipoAtraccion)`, y `Atraccion.tieneCapacidadDisponible(int)` / `tieneCapacidadDisponible(LocalDate, int)`.
- SIA-6 (sobreescritura en 2 clases): `Atraccion` y `Reserva` sobreescriben `toString()`, `equals()` y `hashCode()`.
- SIA-9 (funcionalidad propia): `generarReporteOcupacion(desde, hasta)` en GestorParque, filtrado por rango de fechas.
- SIA-11 (persistencia): `PersistenciaCSV`, batch: carga al iniciar, guarda al salir.
- SIA-12 (excepciones propias con try-catch): `CapacidadExcedidaException`, `ElementoNoEncontradoException`, manejadas en `MenuConsola.iniciar()`.
- SIA-10 (consola + ventana): se elige al iniciar el programa (`Main`).

## Requisitos OPCIONALES, donde estan
- **SIA-O1 (componente grafico estadistico)**: `PanelGraficoBarras`, un JPanel que dibuja a mano (con `Graphics2D`, sin librerias externas) un grafico de barras con el % de ocupacion de cada atraccion. Se ve en la pestaña "3. Estadisticas y ocupacion" de la ventana. El color de la barra cambia segun que tan llena esta (verde/naranjo/rojo).
- **SIA-O2 (generar planilla de calculo)**: `ParqueController.exportarPlanillaOcupacion(desde, hasta)` genera un archivo `reportes/planilla_ocupacion.csv`. Se puede abrir directo con Excel o LibreOffice Calc (usa `;` como separador porque es lo que reconoce el Excel en español). Disponible tanto en la consola (opcion 12) como en la ventana (boton "Exportar planilla").
- **SIA-O3 (Javadoc)**: los metodos mas importantes de `GestorParque`, `Atraccion`, `PersistenciaCSV` y `ParqueController` tienen comentarios Javadoc con `@param`, `@return` y `@throws`. Para generar la documentacion HTML: click derecho en el proyecto -> Generate Javadoc.
- **SIA-O4 (patron MVC)**: explicado arriba, en la seccion de paquetes. El controlador (`ParqueController`) es el puente: las vistas (`MenuConsola`, `VentanaPrincipal`) nunca llaman directamente a `GestorParque` ni a `PersistenciaCSV`.

## Que falta que hagas TU
- **SIA-1 y SIA-2**: el analisis escrito y el diagrama UML (puedes basarte en las clases `Atraccion`, `Grupo`, `Reserva` ya hechas, dibujando sus atributos y relaciones).
- **SIA-13**: subir el proyecto a GitHub con al menos 6 commits reales durante el desarrollo (no todos juntos al final; ve subiendo a medida que avances o modifiques algo).
- El **reporte escrito** de la pauta, que explique punto por punto cada requerimiento (esta guia de README te puede servir de base, pero redactalo con tus palabras).
- El **acta de coevaluacion** con tu grupo.

## Notas sobre el estilo del codigo
El codigo esta escrito de forma simple a proposito (for-loops en vez de streams/lambdas en varias partes, nombres de variable descriptivos pero sin florituras, comentarios explicando el "por que" de cada decision). La idea es que si te preguntan por que se hizo tal cosa en la correccion, puedas explicarlo con tus palabras porque el razonamiento esta comentado ahi mismo.
