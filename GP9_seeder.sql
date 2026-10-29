-- Datos de prueba para `grupo9_aborigym`.
-- Importar despues de GP9_aborigym.sql 

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

USE `grupo9_aborigym`;


START TRANSACTION;

INSERT INTO `entrenador` (`id_entrenador`, `dni`, `nombre`, `apellido`, `especialidad`, `estado`) VALUES
(1,  '34421846', 'Franco',          'Magallanes',  'Musculación', 1),
(2,  '34877066', 'Jessica',         'Auriol',      'Yoga',        1),
(3,  '35915707', 'Gonzalo Exequiel','Martin Asis', 'Spinning',    1),
(4,  '30334915', 'Omar',            'Matijas',     'Funcional',   1),
(5,  '32145678', 'Juan Gabriel',    'Castro',      'CrossFit',    1),
(6,  '34567891', 'Adriana',         'Serquis',     'Pilates',     1),
(7,  '29876543', 'Camilo',          'Quiroga',     'Boxeo',       0),
(8,  '36789124', 'María',           'Funes',       'Zumba',       1),
(9,  '31234567', 'Fidel',           'Gatica',      'Musculación', 1),
(10, '33456789', 'Ariadna',         'Saa',         'Yoga',        1);

INSERT INTO `socio` (`id_socio`, `dni`, `nombre`, `apellido`, `edad`, `correo`, `telefono`, `estado`) VALUES
(1,  '28765432', 'Rodrigo',  'Rodríguez Raineri', 46, 'rodrigo.rodriguez@gmail.com', '2664112001', 0),
(2,  '38901234', 'Salvador', 'Allende',           31, 'salvador.allende@gmail.com',  '2664112002', 1),
(3,  '30123456', 'Gustavo',  'Giménez',           43, 'gustavo.gimenez@gmail.com',   '2664112003', 1),
(4,  '35678901', 'Sofía',    'González',          35, 'sofia.gonzalez@gmail.com',    '2664112004', 1),
(5,  '32345678', 'José',     'Mujica',            40, 'jose.mujica@gmail.com',       '2664112005', 1),
(6,  '29456781', 'Valeria',  'Lucero Guerrero',   44, 'valeria.lucero@gmail.com',    '2664112006', 1),
(7,  '31890123', 'Ernesto',  'Guevara',           42, 'ernesto.guevara@gmail.com',   '2664112007', 1),
(8,  '39123456', 'Axel',     'Quiroga',           30, 'axel.quiroga@gmail.com',      '2664112008', 1),
(9,  '27654321', 'Sergio',   'Mirabile',          48, 'sergio.mirabile@gmail.com',   '2664112009', 0),
(10, '34789012', 'Paola',    'Gatica',            37, 'paola.gatica@gmail.com',      '2664112010', 1),
(11, '30987654', 'Ignacio',  'Da Silva',          43, 'ignacio.dasilva@gmail.com',   '2664112011', 1),
(12, '37890123', 'Julieta',  'Martínez Sosa',     32, 'julieta.martinez@gmail.com',  '2664112012', 1),
(13, '28567890', 'Federico', 'Giménez',           45, 'federico.gimenez@gmail.com',  '2664112013', 0),
(14, '33451267', 'Natalia',  'González',          38, 'natalia.gonzalez@gmail.com',  '2664112014', 1),
(15, '29765431', 'Pablo',    'Pérez Lucero',      44, 'pablo.perez@gmail.com',       '2664112015', 1),
(16, '36123457', 'Andrea',   'Sosa',              33, 'andrea.sosa@gmail.com',       '2664112016', 1),
(17, '31567890', 'Hernán',   'Quiroga Funes',     42, 'hernan.quiroga@gmail.com',    '2664112017', 1),
(18, '28901234', 'Marcela',  'Lucero',            46, 'marcela.lucero@gmail.com',    '2664112018', 0);

-- horario fijo de 1 hora. Camilo (baja) y una clase de Adriana quedan inactivas.
INSERT INTO `clasegym` (`id_clase`, `disciplina`, `id_entrenador`, `horario`, `capacidad`, `estado`) VALUES
(1,  'Musculación', 1,  '08:00:00', 15, 1),
(2,  'Musculación', 1,  '18:00:00', 20, 1),
(3,  'Yoga',        2,  '09:00:00', 12, 1),
(4,  'Yoga',        2,  '19:00:00', 12, 1),
(5,  'Spinning',    3,  '07:00:00', 20, 1),
(6,  'Spinning',    3,  '18:30:00', 18, 1),
(7,  'Funcional',   4,  '10:00:00', 16, 1),
(8,  'Funcional',   4,  '19:00:00', 16, 1),
(9,  'CrossFit',    5,  '08:00:00', 12, 1),
(10, 'CrossFit',    5,  '17:00:00', 12, 1),
(11, 'Pilates',     6,  '11:00:00', 10, 1),
(12, 'Pilates',     6,  '18:00:00', 10, 0),
(13, 'Boxeo',       7,  '20:00:00', 10, 0),
(14, 'Zumba',       8,  '10:00:00', 25, 1),
(15, 'Zumba',       8,  '19:30:00', 25, 1),
(16, 'Musculación', 9,  '09:00:00', 15, 1),
(17, 'Musculación', 9,  '20:00:00', 15, 1),
(18, 'Yoga',        10, '08:30:00', 12, 1),
(19, 'Yoga',        10, '17:30:00', 12, 1);

-- cantidad_entradas = restantes. Costo inventado: 8 -> 15000, 12 -> 20000, 20 -> 30000.
-- Los pases 3 y 5 son anteriores (vencidos) de Sofía y José, que tambien tienen uno vigente.
INSERT INTO `pase` (`id_pase`, `id_socio`, `cantidad_entradas`, `fecha_inicio`, `fecha_fin`, `costo`, `estado`) VALUES
(1,  2,  9,  '2026-10-10', '2026-11-09', 20000.00, 1),
(2,  3,  6,  '2026-10-10', '2026-11-09', 15000.00, 1),
(3,  4,  9,  '2026-08-01', '2026-08-31', 20000.00, 0),
(4,  4,  15, '2026-10-10', '2026-11-09', 30000.00, 1),
(5,  5,  6,  '2026-09-01', '2026-10-01', 15000.00, 0),
(6,  5,  8,  '2026-10-10', '2026-11-09', 20000.00, 1),
(7,  6,  7,  '2026-10-05', '2026-11-04', 15000.00, 1),
(8,  7,  14, '2026-10-10', '2026-11-09', 30000.00, 1),
(9,  8,  10, '2026-10-12', '2026-11-11', 20000.00, 1),
(10, 10, 5,  '2026-10-08', '2026-11-07', 15000.00, 1),
(11, 11, 16, '2026-10-01', '2026-10-31', 30000.00, 1),
(12, 12, 11, '2026-10-15', '2026-11-14', 20000.00, 1),
(13, 14, 1,  '2026-10-01', '2026-10-31', 15000.00, 1),
(14, 15, 12, '2026-10-20', '2026-11-19', 20000.00, 1),
(15, 16, 18, '2026-10-10', '2026-11-09', 30000.00, 1),
(16, 17, 0,  '2026-10-01', '2026-10-31', 15000.00, 1),
(17, 1,  10, '2026-07-01', '2026-07-31', 20000.00, 0),
(18, 9,  8,  '2026-06-01', '2026-07-01', 15000.00, 0),
(19, 13, 11, '2026-08-15', '2026-09-14', 20000.00, 0),
(20, 18, 7,  '2026-05-20', '2026-06-19', 15000.00, 0);

-- Cada fila es una entrada ya consumida. Hernán (pase 16) agoto las 8.
-- Pablo (pase 14) y Sergio (pase 18) todavia no vinieron.
-- El 2026-10-29 hay varias clases con gente, para probar el listado del dia.
INSERT INTO `asistencia` (`id_socio`, `id_clase`, `id_pase`, `fecha_asistencia`) VALUES
(2,  5,  1,  '2026-10-22'),
(2,  1,  1,  '2026-10-27'),
(2,  5,  1,  '2026-10-29'),

(3,  7,  2,  '2026-10-24'),
(3,  9,  2,  '2026-10-29'),

(4,  3,  3,  '2026-08-05'),
(4,  14, 3,  '2026-08-12'),
(4,  4,  3,  '2026-08-19'),
(4,  3,  4,  '2026-10-20'),
(4,  14, 4,  '2026-10-22'),
(4,  4,  4,  '2026-10-24'),
(4,  15, 4,  '2026-10-27'),
(4,  3,  4,  '2026-10-29'),

(5,  9,  5,  '2026-09-08'),
(5,  10, 5,  '2026-09-15'),
(5,  9,  6,  '2026-10-20'),
(5,  10, 6,  '2026-10-22'),
(5,  7,  6,  '2026-10-27'),
(5,  10, 6,  '2026-10-29'),

(6,  11, 7,  '2026-10-28'),

(7,  2,  8,  '2026-10-20'),
(7,  16, 8,  '2026-10-22'),
(7,  6,  8,  '2026-10-24'),
(7,  17, 8,  '2026-10-27'),
(7,  2,  8,  '2026-10-28'),
(7,  16, 8,  '2026-10-29'),

(8,  5,  9,  '2026-10-24'),
(8,  6,  9,  '2026-10-29'),

(10, 14, 10, '2026-10-20'),
(10, 18, 10, '2026-10-27'),
(10, 14, 10, '2026-10-29'),

(11, 1,  11, '2026-10-22'),
(11, 8,  11, '2026-10-24'),
(11, 2,  11, '2026-10-27'),
(11, 8,  11, '2026-10-29'),

(12, 4,  12, '2026-10-29'),

(14, 11, 13, '2026-10-20'),
(14, 3,  13, '2026-10-22'),
(14, 18, 13, '2026-10-24'),
(14, 19, 13, '2026-10-26'),
(14, 11, 13, '2026-10-27'),
(14, 4,  13, '2026-10-28'),
(14, 19, 13, '2026-10-29'),

(16, 15, 15, '2026-10-24'),
(16, 14, 15, '2026-10-28'),

(17, 9,  16, '2026-10-15'),
(17, 10, 16, '2026-10-16'),
(17, 7,  16, '2026-10-20'),
(17, 8,  16, '2026-10-21'),
(17, 9,  16, '2026-10-22'),
(17, 1,  16, '2026-10-24'),
(17, 5,  16, '2026-10-27'),
(17, 6,  16, '2026-10-28'),

(1,  2,  17, '2026-07-06'),
(1,  16, 17, '2026-07-13'),

(13, 7,  19, '2026-08-20'),

(18, 11, 20, '2026-05-25');

COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
