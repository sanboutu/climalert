package ar.edu.utn.frba.ddsi.climalert.services.impl;

import ar.edu.utn.frba.ddsi.climalert.client.WeatherApiClient;
import ar.edu.utn.frba.ddsi.climalert.dto.weatherapi.WeatherApiResponseDto;
import ar.edu.utn.frba.ddsi.climalert.models.entities.RegistroClimatico;
import ar.edu.utn.frba.ddsi.climalert.models.repositories.RegistroClimaticoRepository;
import ar.edu.utn.frba.ddsi.climalert.services.ClimaPollingService;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClimaPollingServiceImpl implements ClimaPollingService {

  private final WeatherApiClient weatherApiClient;
  private final RegistroClimaticoRepository repository;

  public ClimaPollingServiceImpl(
      WeatherApiClient weatherApiClient, RegistroClimaticoRepository repository) {
    this.weatherApiClient = weatherApiClient;
    this.repository = repository;
  }

  @Override
  public void registrarClimaActual() {
    WeatherApiResponseDto respuesta = weatherApiClient.obtenerClimaActual();

    log.info(
        "Clima polleado de WeatherAPI - ubicacion: {}, temperatura: {}°C, sensacion termica: {}°C, "
            + "humedad: {}%, condicion: {}, viento: {} km/h {}, presion: {} hPa, uv: {}, "
            + "nubosidad: {}%, precipitacion: {} mm",
        respuesta.location().name(),
        respuesta.current().tempC(),
        respuesta.current().feelslikeC(),
        respuesta.current().humidity(),
        respuesta.current().condition().text(),
        respuesta.current().windKph(),
        respuesta.current().windDir(),
        respuesta.current().pressureMb(),
        respuesta.current().uv(),
        respuesta.current().cloud(),
        respuesta.current().precipMm());

    RegistroClimatico registro = RegistroClimatico.builder()
        .fechaHora(LocalDateTime.now())
        .ubicacion(respuesta.location().name())
        .temperatura(respuesta.current().tempC())
        .sensacionTermica(respuesta.current().feelslikeC())
        .humedad(respuesta.current().humidity())
        .condicion(respuesta.current().condition().text())
        .velocidadViento(respuesta.current().windKph())
        .direccionViento(respuesta.current().windDir())
        .presion(respuesta.current().pressureMb())
        .indiceUv(respuesta.current().uv())
        .nubosidad(respuesta.current().cloud())
        .precipitacion(respuesta.current().precipMm())
        .build();

    repository.guardar(registro);
  }
}