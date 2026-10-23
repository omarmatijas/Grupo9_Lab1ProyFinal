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
- Maven (proyecto NetBeans)
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
│   └── Asistencia.java
├── Persistencia/    Acceso a datos
│   ├── Conexion.java
│   └── SocioData.java
└── Vista/
    └── Aborigym.java   Clase main con test de consola
```

## Base de datos
Script: `GP9_aborigym.sql` (crea la base `grupo9_aborigym`).

| Tabla | Descripción |
|---|---|
| `socio` | Datos personales y de contacto. DNI y correo únicos |
| `entrenador` | Instructores y su especialidad. DNI único |
| `clasegym` | Disciplina, horario, capacidad y entrenador asignado |
| `pase` | Paquete de entradas de un socio, con vencimiento a 30 días |
| `asistencia` | Registro de un socio en una clase, asociado al pase que consume |

Todas las entidades tienen un campo `estado` para la baja lógica.

## Cómo ejecutar
1. Iniciar Apache y MySQL en XAMPP.
2. Importar `GP9_aborigym.sql` desde phpMyAdmin.
3. Abrir la carpeta `aborigym` en NetBeans.
4. Clic derecho en el proyecto → **Clean and Build** (descarga el driver de MariaDB).
5. **Run**: ejecuta `com.prog.aborigym.Vista.Aborigym`.

Datos de conexión (en `Vista/Aborigym.java`): `localhost:3306`, base `grupo9_aborigym`,
usuario `root`, sin contraseña.

## Avance

### Primera entrega
- [x] Script de la base de datos
- [ ] Captura de la vista diseñador de la BD
- [ ] Diagrama de clases UML
- [x] Package `Modelo` con las 5 entidades
- [x] Clase `Conexion` a MariaDB
- [x] Package `Persistencia` con `SocioData`
  (guardar, actualizar, obtener por id, buscar por DNI, baja y alta lógica)
- [x] Package `Vista` con main de prueba: conecta a la base e ingresa a los integrantes del grupo como socios

### Pendiente (próximas entregas)
- [ ] `EntrenadorData`, `ClaseGymData`, `PaseData`, `AsistenciaData`
- [ ] Validaciones: cupo de la clase, pase vigente, entradas disponibles, horario del entrenador sin superposición
- [ ] Listados y consultas SQL del enunciado
- [ ] Interfaz gráfica
