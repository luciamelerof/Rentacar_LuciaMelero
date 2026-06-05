# GestorAlquiler

**Alumna:** Lucía Melero  
**Módulo:** Programación  
**Curso:** 2024–2025  
**Repositorio:** https://github.com/luciamelerof/gestoralquiler_luciamelero.git

---

## ¿Qué es esto?

GestorAlquiler es una app de escritorio en Java para gestionar una empresa de alquiler de coches. Desarrollada como proyecto final del módulo de Programación.

Desde la aplicación se puede gestionar todo: los vehículos de la flota, los clientes y empleados, y los alquileres. También muestra en tiempo real el precio de cada vehículo convertido a otras monedas usando una API externa.

---

## Tecnologías usadas

- Java 17
- MySQL 8 con JDBC
- Java Swing para la interfaz
- Patrón DAO + MVC
- ExchangeRate-API para conversión de divisas
- BCrypt para contraseñas

---

## Estructura del proyecto

```
src/
  api/    → ExchangeRateService.java
  db/     → ConexionDB.java (Singleton)
  model/  → Usuario, Cliente, Empleado, Vehiculo, Alquiler
  dao/    → Interfaces + implementaciones (una por entidad)
  dto/    → AlquilerDTO (para consultas con JOIN)
  view/   → Login, Registro, VentanaPrincipal
  Main.java

lib/      → JARs externos (MySQL connector, BCrypt)
sql/      → rentacar.sql
documentacion/
```

---

## Base de datos

La base de datos se llama `rentacar` y sigue el patrón **Joined Table Inheritance**:

- `usuarios` es la tabla raíz con los campos comunes (username, password, email, nombre, apellidos, dni, rol)
- `clientes` y `empleados` son tablas hijas que amplían usuarios mediante FK primaria
- `vehiculos` es la entidad principal
- `alquileres` es la tabla de relación N:M entre clientes y vehículos, con fecha, precio total y estado

Todas las relaciones usan `ON DELETE CASCADE`.

---

## Cómo ejecutarlo

**Requisitos:** Java 17+, MySQL 8, VS Code con Extension Pack for Java

1. Ejecuta `sql/rentacar.sql` en MySQL para crear la base de datos
2. Abre `src/db/ConexionDB.java` y pon tu contraseña de MySQL si tienes una
3. Abre la carpeta en VS Code y pulsa F5

**Usuarios de prueba** (contraseña `1234` para todos):

| Usuario | Rol |
|---------|-----|
| admin | empleado |
| laura | empleado |
| maria | cliente |
| juan | cliente |

---

## Qué hace cada parte

**Login.java** — Valida usuario y contraseña contra la BD. Si son correctos abre el dashboard.

**Registro.java** — Formulario que cambia los campos según el rol elegido (cliente o empleado). Usa una transacción para insertar en `usuarios` y en la tabla hija a la vez, con rollback si algo falla.

**VentanaPrincipal.java** — Panel principal con tres módulos:
- Vehículos: CRUD completo + conversor de divisas
- Alquileres: crear, editar y eliminar alquileres (con JOIN para mostrar nombres en vez de IDs)
- Usuarios: ver y editar datos de todos los usuarios

**ExchangeRateService.java** — Llama a la API de ExchangeRate con una petición HTTP simple. No usa librerías externas, solo `HttpURLConnection`. La llamada va en un hilo separado para no bloquear la interfaz.

---

## Patrón DAO

Cada entidad tiene su interfaz (`IUsuarioDAO`, `IVehiculoDAO`, `IAlquilerDAO`) y su implementación. Las vistas no tienen ni una línea de SQL, todo pasa por los DAOs.

Buenas prácticas aplicadas:
- `try-with-resources` en todos los bloques JDBC
- `PreparedStatement` siempre, sin concatenar SQL
- `setAutoCommit(false)` / `commit()` / `rollback()` en operaciones multi-tabla

---

## Extensión: conversión de divisas

En el módulo de vehículos hay un panel que convierte el precio del vehículo seleccionado a USD, GBP, JPY, CHF o MXN. Los tipos de cambio se obtienen en tiempo real desde ExchangeRate-API (capa gratuita, sin dependencias adicionales).

---
