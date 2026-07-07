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
        "Clima polleado de WeatherAPI - ubicacion: {}, temperatura: {}°C, humedad: {}%, "
            + "condicion: {}",
        respuesta.location().name(),
        respuesta.current().tempC(),
        respuesta.current().humidity(),
        respuesta.current().condition().text());

    RegistroClimatico registro = RegistroClimatico.builder()
        .fechaHora(LocalDateTime.now())
        .ubicacion(respuesta.location().name())
        .temperatura(respuesta.current().tempC())
        .humedad(respuesta.current().humidity())
        .condicion(respuesta.current().condition().text())
        .build();

    repository.guardar(registro);
  }
}