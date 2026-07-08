package ar.edu.utn.frba.ddsi.climalert;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import ar.edu.utn.frba.ddsi.climalert.models.entities.RegistroClimatico;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RegistroClimaticoTest {

  private RegistroClimatico crearRegistro(double temperatura, int humedad) {
    return RegistroClimatico.builder()
        .fechaHora(LocalDateTime.now())
        .ubicacion("Buenos Aires")
        .temperatura(temperatura)
        .humedad(humedad)
        .condicion("Clear")
        .build();
  }

  @Test
  void esCondicionCriticaCuandoSuperaAmbosUmbrales() {
    RegistroClimatico registro = crearRegistro(36.0, 65);

    assertThat(registro.esCondicionCritica(35.0, 60)).isTrue();
  }

  @Test
  void noEsCondicionCriticaCuandoSoloSuperaTemperatura() {
    RegistroClimatico registro = crearRegistro(36.0, 50);

    assertThat(registro.esCondicionCritica(35.0, 60)).isFalse();
  }

  @Test
  void noEsCondicionCriticaCuandoSoloSuperaHumedad() {
    RegistroClimatico registro = crearRegistro(20.0, 65);

    assertThat(registro.esCondicionCritica(35.0, 60)).isFalse();
  }

  @Test
  void noEsCondicionCriticaCuandoNoSuperaNingunUmbral() {
    RegistroClimatico registro = crearRegistro(20.0, 40);

    assertThat(registro.esCondicionCritica(35.0, 60)).isFalse();
  }

  @Test
  void noEsCondicionCriticaEnLosValoresLimite() {
    RegistroClimatico registro = crearRegistro(35.0, 60);

    assertThat(registro.esCondicionCritica(35.0, 60)).isFalse();
  }
}
