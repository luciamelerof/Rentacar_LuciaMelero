DROP DATABASE IF EXISTS rentacar;

CREATE DATABASE rentacar CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE rentacar;

CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    dni VARCHAR(20) NOT NULL UNIQUE,
    rol ENUM('cliente', 'empleado') NOT NULL DEFAULT 'cliente'
);

CREATE TABLE clientes (
    usuario_id INT PRIMARY KEY,
    telefono VARCHAR(20),
    direccion VARCHAR(200),
    carnet_conducir VARCHAR(20) NOT NULL,
    CONSTRAINT fk_cliente FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE
);

CREATE TABLE empleados (
    usuario_id INT PRIMARY KEY,
    salario DECIMAL(8, 2) NOT NULL DEFAULT 1500.00,
    fecha_alta DATE NOT NULL,
    CONSTRAINT fk_empleado FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE
);

CREATE TABLE vehiculos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    matricula VARCHAR(10) NOT NULL UNIQUE,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    anio INT NOT NULL,
    categoria ENUM(
        'turismo',
        'SUV',
        'furgoneta',
        'deportivo'
    ) NOT NULL DEFAULT 'turismo',
    precio_dia DECIMAL(8, 2) NOT NULL,
    disponible BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE alquileres (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT NOT NULL,
    vehiculo_id INT NOT NULL,
    empleado_id INT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    precio_total DECIMAL(10, 2) NOT NULL,
    estado ENUM(
        'activo',
        'finalizado',
        'cancelado'
    ) NOT NULL DEFAULT 'activo',
    CONSTRAINT fk_alq_cliente FOREIGN KEY (cliente_id) REFERENCES clientes (usuario_id) ON DELETE CASCADE,
    CONSTRAINT fk_alq_vehiculo FOREIGN KEY (vehiculo_id) REFERENCES vehiculos (id) ON DELETE CASCADE,
    CONSTRAINT fk_alq_empleado FOREIGN KEY (empleado_id) REFERENCES empleados (usuario_id) ON DELETE CASCADE
);

INSERT INTO
    usuarios
VALUES (
        1,
        'admin',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'admin@rentacar.com',
        'Carlos',
        'López Ruiz',
        '12345678A',
        'empleado'
    ),
    (
        2,
        'maria',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'maria@email.com',
        'María',
        'García Pérez',
        '87654321B',
        'cliente'
    ),
    (
        3,
        'juan',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'juan@email.com',
        'Juan',
        'Martínez Díaz',
        '11111111C',
        'cliente'
    ),
    (
        4,
        'laura',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'laura@rentacar.com',
        'Laura',
        'Sánchez Mora',
        '22222222D',
        'empleado'
    );

INSERT INTO
    empleados
VALUES (1, 2200.00, '2022-01-10'),
    (4, 1800.00, '2023-03-15');

INSERT INTO
    clientes
VALUES (
        2,
        '600111222',
        'Calle Mayor 5, Granada',
        'GR123456'
    ),
    (
        3,
        '600333444',
        'Av. Constitución 10, Sevilla',
        'SE789012'
    );

INSERT INTO
    vehiculos
VALUES (
        1,
        '1234ABC',
        'Toyota',
        'Corolla',
        2021,
        'turismo',
        45.00,
        TRUE
    ),
    (
        2,
        '5678DEF',
        'Ford',
        'Kuga',
        2022,
        'SUV',
        65.00,
        TRUE
    ),
    (
        3,
        '9012GHI',
        'Renault',
        'Trafic',
        2020,
        'furgoneta',
        80.00,
        FALSE
    ),
    (
        4,
        '3456JKL',
        'BMW',
        'M3',
        2023,
        'deportivo',
        120.00,
        TRUE
    );

INSERT INTO
    alquileres
VALUES (
        1,
        2,
        3,
        1,
        '2025-05-01',
        '2025-05-05',
        180.00,
        'finalizado'
    ),
    (
        2,
        3,
        2,
        4,
        '2025-05-10',
        '2025-05-15',
        325.00,
        'activo'
    );