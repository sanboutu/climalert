package ar.edu.utn.frba.ddsi.climalert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.edu.utn.frba.ddsi.climalert.client.WeatherApiClient;
import ar.edu.utn.frba.ddsi.climalert.dto.weatherapi.WeatherApiResponseDto;
import ar.edu.utn.frba.ddsi.climalert.models.entities.RegistroClimatico;
import ar.edu.utn.frba.ddsi.climalert.models.repositories.RegistroClimaticoRepository;
import ar.edu.utn.frba.ddsi.climalert.models.repositories.RegistroClimaticoRepository;
import ar.edu.utn.frba.ddsi.climalert.services.impl.ClimaPollingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ClimaPollingServiceImplTest {

  @Mock
  private WeatherApiClient weatherApiClient;

  @Mock
  private RegistroClimaticoRepository repository;

  private ClimaPollingServiceImpl service;

  @Test
  void registraElClimaObtenidoDeWeatherApi() {
    service = new ClimaPollingServiceImpl(weatherApiClient, repository);

    WeatherApiResponseDto.Location location = new WeatherApiResponseDto.Location("Ciudad Evita");
    WeatherApiResponseDto.Condition condition = new WeatherApiResponseDto.Condition("Clear");
    WeatherApiResponseDto.Current current = new WeatherApiResponseDto.Current(
        36.5, 38.0, 65, condition, 13.0, "NW", 1014.0, 0.2, 0, 0.0, "2026-07-03 18:30");
    WeatherApiResponseDto respuesta = new WeatherApiResponseDto(location, current);

    when(weatherApiClient.obtenerClimaActual()).thenReturn(respuesta);

    service.registrarClimaActual();

    ArgumentCaptor<RegistroClimatico> captor = ArgumentCaptor.forClass(RegistroClimatico.class);
    verify(repository).guardar(captor.capture());

    RegistroClimatico registroGuardado = captor.getValue();
    assertThat(registroGuardado.getUbicacion()).isEqualTo("Ciudad Evita");
    assertThat(registroGuardado.getTemperatura()).isEqualTo(36.5);
    assertThat(registroGuardado.getSensacionTermica()).isEqualTo(38.0);
    assertThat(registroGuardado.getHumedad()).isEqualTo(65);
    assertThat(registroGuardado.getCondicion()).isEqualTo("Clear");
    assertThat(registroGuardado.getVelocidadViento()).isEqualTo(13.0);
    assertThat(registroGuardado.getDireccionViento()).isEqualTo("NW");
    assertThat(registroGuardado.getPresion()).isEqualTo(1014.0);
    assertThat(registroGuardado.getIndiceUv()).isEqualTo(0.2);
    assertThat(registroGuardado.getNubosidad()).isEqualTo(0);
    assertThat(registroGuardado.getPrecipitacion()).isEqualTo(0.0);
  }
}
