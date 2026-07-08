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
- Cuenta de [Mailtrap](https://mailtrap.io/) (o cualquier servidor SMTP) para el envío de correos

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

1. **Integración con WeatherAPI**: cada 5 minutos, el sistema consulta el endpoint `/current.json` de WeatherAPI para una ubicación fija (Buenos Aires) y almacena los datos localmente para registro histórico.
2. **Procesamiento de alertas**: cada 1 minuto, el sistema analiza el último registro climático disponible. Para evitar alertar múltiples veces sobre el mismo dato (ya que el polling corre cada 5 minutos pero el análisis cada 1), el sistema recuerda el identificador del último registro ya analizado y omite el reanálisis si no llegó información nueva.
3. **Notificación por correo**: al generarse una alerta, se envía un correo con el detalle completo del clima (temperatura, sensación térmica, humedad, condición, viento, presión, índice UV, nubosidad y precipitación) a:
   - admin@clima.com
   - emergencias@clima.com
   - meteorologia@clima.com

El polling y el análisis están unificados en un único scheduler (`ClimalertScheduler`), que corre cada minuto vía cron y decide internamente si también le corresponde pollear en ese ciclo — esto garantiza que, en los minutos donde coinciden ambas tareas, el polling se ejecute siempre antes que el análisis.

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

El servicio usa el profile `local` para cargar configuración sensible desde `climalert-service/src/main/resources/application-local.properties` (archivo no versionado, excluido en `.gitignore`). Crear ese archivo con el siguiente contenido antes de ejecutar el servicio:

```properties
weatherapi.base-url=https://api.weatherapi.com/v1
weatherapi.api-key=${WEATHER_API_KEY:sin-configurar}
weatherapi.location=Buenos Aires

climalert.alerta.temperatura-umbral=35
climalert.alerta.humedad-umbral=60
climalert.alerta.destinatarios=admin@clima.com,emergencias@clima.com,meteorologia@clima.com

spring.mail.host=sandbox.smtp.mailtrap.io
spring.mail.port=2525
spring.mail.username=${MAILTRAP_USERNAME:sin-configurar}
spring.mail.password=${MAILTRAP_PASSWORD:sin-configurar}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

Las credenciales reales se toman de variables de entorno del sistema (`WEATHER_API_KEY`, `MAILTRAP_USERNAME`, `MAILTRAP_PASSWORD`), configurables en el Run/Debug Configuration del IDE o directamente en el entorno de ejecución. Sin estas variables, el servicio arranca igual (con valores por defecto `sin-configurar`), pero la integración real con WeatherAPI y el envío de correos van a fallar.

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

### Ejecutar los tests

```bash
mvn test
```

### Validar el proyecto de forma exhaustiva

```bash
mvn clean verify
```

Este comando ejecuta los tests, valida las convenciones de formato mediante Checkstyle, detecta code smells con SpotBugs y valida la cobertura del proyecto con Jacoco.

---

## Entrega del proyecto

Antes de entregar, correr la validación completa y crear el tag `entrega-final`:

```bash
mvn clean verify && git tag entrega-final && git push origin HEAD --tags
```

---

## Estado del proyecto

Servicio Spring Boot funcional y validado de punta a punta: integración real con WeatherAPI, scheduling unificado (polling + análisis) con deduplicación de alertas, y envío de correos confirmado contra Mailtrap. Suite de tests unitarios cubriendo modelo, repositorio, servicios y scheduler, con `mvn clean verify` en verde (Checkstyle, SpotBugs y Jacoco).