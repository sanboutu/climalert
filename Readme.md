# Climalert

Sistema de monitoreo climático y envío automático de alertas, desarrollado como trabajo práctico de la cátedra DDSI (UTN FRBA).

Climalert es un servicio autónomo, sin interfaz gráfica, que se conecta periódicamente a un proveedor meteorológico externo, procesa los datos recibidos y notifica por correo electrónico cuando se detectan condiciones climáticas peligrosas o inusuales.

**Condición de alerta (primera iteración):** temperatura mayor a 35° y humedad superior al 60%.

---

## Requisitos previos

- JDK 21
- Maven 3.9+
- Docker (opcional, solo para construir y ejecutar el contenedor)
- API key de [WeatherAPI](https://www.weatherapi.com/)

---

## Estructura del repositorio

```
climalert/
├── pom.xml                    # POM padre: versiones y dependencyManagement
├── common-lib/                # Librería compartida (JAR), reservada para código común
└── climalert-service/         # Servicio Climalert — puerto 8080
```

`climalert-service` declara `common-lib` como dependencia local del reactor, aunque en esta iteración `common-lib` todavía no contiene código: se mantiene la estructura multi-módulo previendo una futura extensión del sistema con más servicios.

---

## Funcionamiento

1. **Integración con WeatherAPI**: cada 5 minutos, el sistema consulta el endpoint `/current.json` de WeatherAPI para una ubicación fija (CABA) y almacena los datos localmente para registro histórico.
2. **Procesamiento de alertas**: cada 1 minuto, el sistema analiza la última información climática disponible y evalúa si se cumplen las condiciones de alerta.
3. **Notificación por correo**: al generarse una alerta, se envía un correo con el detalle completo del clima a:
    - admin@clima.com
    - emergencias@clima.com
    - meteorologia@clima.com

---

## Tecnologías

| Tecnología          | Versión       |
|---------------------|---------------|
| Java                | 21            |
| Spring Boot         | 4.0.5         |
| Spring Cloud BOM    | 2025.1.1      |
| Lombok              | 1.18.34       |
| Maven               | 3.9+          |

---

## Configuración

Antes de ejecutar el servicio, configurar las siguientes variables (ver `application.properties` / `application-local.properties`):

- API key de WeatherAPI
- Credenciales del servidor de correo saliente (SMTP)

---

## Desarrollo local (Maven)

Todos los comandos se ejecutan desde la **raíz del proyecto**.

### Compilar todos los módulos

```bash
mvn clean install
```

### Ejecutar el servicio

```bash
mvn spring-boot:run -pl climalert-service
```

El servicio queda disponible en el puerto **8080**.

### Validar el proyecto de forma exhaustiva

```bash
mvn clean verify
```

Este comando ejecuta los tests, valida las convenciones de formato mediante Checkstyle, detecta code smells con SpotBugs y valida la cobertura del proyecto con Jacoco.

---

## Construcción de la imagen Docker

Este proyecto utiliza una arquitectura multi-módulo de Maven, por lo que **el contexto de construcción de Docker siempre debe ser la raíz del proyecto**.

```bash
docker build -t climalert-img -f climalert-service/Dockerfile .
```

### Ejecutar el contenedor

```bash
docker run -p 8080:8080 climalert-img
```

---

## Entrega del proyecto

Antes de entregar, correr la validación completa y crear el tag `entrega-final`:

```bash
mvn clean verify && git tag entrega-final && git push origin HEAD --tags
```

---

## Estado del proyecto

Servicio Spring Boot funcional con estructura multi-módulo validada (`mvn clean verify` en verde). Pendiente: integración con WeatherAPI, lógica de scheduling y análisis de alertas, y envío de notificaciones por correo.