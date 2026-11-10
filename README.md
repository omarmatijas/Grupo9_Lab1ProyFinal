# Grupo9_Lab1ProyFinal
Proyecto final 2026 - TUDS - Laboratorio 1

## AboriGym
Sistema de gestión para el gimnasio **AboriGym**: administración de socios, entrenadores,
clases, pases y asistencias.

## Integrantes - Grupo 9
- Omar Matijas
- Gonzalo Martin
- Franco Magallanes
- Jessica Auriol

## Tecnologías
- Java 17
- Maven (proyecto NetBeans) con interfaz gráfica Swing
- MariaDB / MySQL (XAMPP - phpMyAdmin)
- Driver JDBC `mariadb-java-client` 3.4.1

## Estructura del proyecto
```
aborigym/src/main/java/com/prog/aborigym/
├── Modelo/          Entidades del dominio
│   ├── Socio.java
│   ├── Entrenador.java
│   ├── ClaseGym.java
│   ├── Pase.java
│   ├── Asistencia.java
│   └── Disciplinas.java          Lista de disciplinas y especialidades
├── Persistencia/    Acceso a datos (SQL con PreparedStatement)
│   ├── Conexion.java
│   ├── SocioData.java
│   ├── EntrenadorData.java
│   ├── ClaseGymData.java
│   ├── PaseData.java
│   └── AsistenciaData.java
└── Vista/           Interfaz gráfica (JInternalFrame dentro de MenuPrincipal)
    ├── MenuPrincipal.java        Clase principal: menú y conexión a la base
    ├── VistaSocios.java          ABM de socios
    ├── VistaEntrenadores.java    ABM de entrenadores
    ├── VistaClases.java          ABM de clases
    ├── VistaPases.java           ABM de pases e historial del pase vigente
    ├── MarcarAsistencia.java     Marcar y anular asistencias
    ├── ConsultarAsistencia.java  Asistentes y lugares de una clase en una fecha
    ├── FiltroClases.java         Filtro de clases compartido por varias pantallas
    ├── Validaciones.java         Validaciones de datos comunes a todas las pantallas
    └── Aborigym.java             Prueba por consola (primera entrega)
```

Diagrama de la base de datos: `diagrama.jpg`.

## Base de datos
| Script | Uso |
|---|---|
| `GP9_aborigym.sql` | Crea la base `grupo9_aborigym` con sus 5 tablas |
| `GP9_seeder.sql` | Carga datos de prueba (importar después del anterior) |

| Tabla | Descripción |
|---|---|
| `socio` | Datos personales y de contacto. DNI y correo únicos |
| `entrenador` | Instructores y su especialidad. DNI único |
| `clasegym` | Disciplina, horario, capacidad y entrenador asignado |
| `pase` | Paquete de 8, 12 o 20 entradas de un socio, con vencimiento a 30 días |
| `asistencia` | Registro de un socio en una clase, asociado al pase que consume |

`socio`, `entrenador`, `clasegym` y `pase` tienen un campo `estado` para la baja lógica.
Anular una asistencia la elimina y devuelve la entrada al pase.

**Importante:** los pases del seeder tienen fechas fijas. Si ya vencieron, cargar pases nuevos
desde Gestión de pases (o actualizar sus fechas en el seeder) para poder marcar asistencias.

## Cómo ejecutar
1. Iniciar MySQL en XAMPP.
2. Desde phpMyAdmin, importar `GP9_aborigym.sql` y después `GP9_seeder.sql`.
3. Abrir la carpeta `aborigym` en NetBeans.
4. Clic derecho en el proyecto → **Clean and Build** (la primera vez descarga el driver de MariaDB).
5. **Run**: abre `com.prog.aborigym.Vista.MenuPrincipal`.

Conexión (en `Vista/MenuPrincipal.java`): `localhost`, base `grupo9_aborigym`, usuario `root`,
sin contraseña. Prueba primero el puerto **3306** y, si no responde, el **3307**.

## Uso
Orden sugerido para cargar datos: Entrenadores → Clases → Socios → Pases → Asistencia.

| Menú | Pantalla | Qué permite |
|---|---|---|
| Socios | Gestión de socios | Alta, modificación, baja y reactivación (casilla Activo). Búsqueda por DNI, nombre o apellido |
| Socios | Gestión de pases | Vender pases de 8, 12 o 20 entradas, ver entradas restantes e historial del pase vigente |
| Entrenadores | Gestión de entrenadores | Alta, modificación, baja lógica, eliminar (solo sin clases) y búsqueda |
| Clases | Gestión de clases | Alta, modificación y baja. Primero se elige la disciplina y se ofrecen los entrenadores de esa especialidad |
| Asistencia | Actualizar asistencia a clase | Marcar o anular la asistencia de un socio a una clase en una fecha |
| Asistencia | Consultar asistencia | Socios asistentes y lugares ocupados de una clase en una fecha |

Las pantallas de gestión funcionan igual: se elige un registro de la tabla, **Nuevo** o **Editar**
habilitan el formulario, y después **Guardar** o **Cancelar**. **Limpiar** quita los filtros de búsqueda.

## Reglas de negocio
- DNI y correo no se repiten.
- Al crear una clase solo se pueden elegir entrenadores con la especialidad de esa disciplina.
- Un entrenador no puede tener dos clases superpuestas (cada clase dura 1 hora).
- Un pase tiene 8, 12 o 20 entradas y vence a los 30 días de su inicio.
- Marcar asistencia exige pase vigente con entradas y lugar en la clase, y descuenta una entrada.
  Un socio no puede marcar dos veces la misma clase el mismo día.
- Dar de baja no borra: deja el registro inactivo.

## Consultas SQL (con JOIN)
| Consulta del enunciado | Método | Pantalla |
|---|---|---|
| 1. Clases con su entrenador y capacidad disponible | `ClaseGymData.listarClasesGym` + `AsistenciaData.contarAsistenciaClases` | Actualizar y Consultar asistencia |
| 2. Historial del socio en el pase actual | `AsistenciaData.historialPaseActual` | Gestión de pases |
| 3. Entradas restantes del pase activo | `PaseData.listarEntradasPaseActivo` / `listarPasesConConsumo` | Gestión de pases |
| 4. Asistencia y concurrencia de una clase en una fecha | `AsistenciaData.listarSocios` + `contarAsistenciaClases` | Consultar asistencia |
| 4 bis. Clases del día por hora, con cupo | `contarAsistenciaClases` + filtro "Solo con vacantes" | Actualizar asistencia |
| 5. Clases por disciplina o por entrenador | `ClaseGymData.listarClasesGymPorDisciplina` / `PorEntrenador` | Gestión de clases |

## Avance
- [x] Primera entrega: base de datos, proyecto, conexión y `SocioData`
- [x] Segunda entrega: clases de persistencia (`...Data`) con SQL embebido e interfaz gráfica
- [x] Tercera entrega: validaciones, listados, consultas con JOIN y ABM completos
- [x] Diagrama de la base de datos
