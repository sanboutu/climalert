package ar.edu.utn.frba.ddsi.climalert;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.edu.utn.frba.ddsi.climalert.models.entities.RegistroClimatico;
import ar.edu.utn.frba.ddsi.climalert.models.repositories.RegistroClimaticoRepository;
import ar.edu.utn.frba.ddsi.climalert.services.AlertaNotificationService;
import java.time.LocalDateTime;
import java.util.Optional;

import ar.edu.utn.frba.ddsi.climalert.services.impl.AlertaAnalisisServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AlertaAnalisisServiceImplTest {

  private static final double UMBRAL_TEMPERATURA = 35.0;
  private static final int UMBRAL_HUMEDAD = 60;

  @Mock
  private RegistroClimaticoRepository repository;

  @Mock
  private AlertaNotificationService notificationService;

  private AlertaAnalisisServiceImpl service;

  private RegistroClimatico crearRegistro(double temperatura, int humedad) {
    RegistroClimatico registro = RegistroClimatico.builder()
        .fechaHora(LocalDateTime.now())
        .ubicacion("Buenos Aires")
        .temperatura(temperatura)
        .humedad(humedad)
        .condicion("Clear")
        .build();
    registro.setId(1L);
    return registro;
  }

  @Test
  void enviaAlertaCuandoElUltimoRegistroEsCritico() {
    service = new AlertaAnalisisServiceImpl(
        repository, notificationService, UMBRAL_TEMPERATURA, UMBRAL_HUMEDAD);
    RegistroClimatico registroCritico = crearRegistro(40.0, 70);
    when(repository.obtenerUltimo()).thenReturn(Optional.of(registroCritico));

    service.analizarUltimoRegistro();

    verify(notificationService).enviarAlerta(registroCritico);
  }

  @Test
  void noEnviaAlertaCuandoElUltimoRegistroNoEsCritico() {
    service = new AlertaAnalisisServiceImpl(
        repository, notificationService, UMBRAL_TEMPERATURA, UMBRAL_HUMEDAD);
    RegistroClimatico registroNormal = crearRegistro(20.0, 40);
    when(repository.obtenerUltimo()).thenReturn(Optional.of(registroNormal));

    service.analizarUltimoRegistro();

    verify(notificationService, never()).enviarAlerta(registroNormal);
  }

  @Test
  void noEnviaAlertaCuandoNoHayRegistros() {
    service = new AlertaAnalisisServiceImpl(
        repository, notificationService, UMBRAL_TEMPERATURA, UMBRAL_HUMEDAD);
    when(repository.obtenerUltimo()).thenReturn(Optional.empty());

    service.analizarUltimoRegistro();

    verify(notificationService, never()).enviarAlerta(org.mockito.ArgumentMatchers.any());
  }

  @Test
  void noReanalizaNiReenviaAlertaSiElRegistroEsElMismoQueLaVezAnterior() {
    service = new AlertaAnalisisServiceImpl(
        repository, notificationService, UMBRAL_TEMPERATURA, UMBRAL_HUMEDAD);
    RegistroClimatico registroCritico = crearRegistro(40.0, 70);
    when(repository.obtenerUltimo()).thenReturn(Optional.of(registroCritico));

    service.analizarUltimoRegistro();
    service.analizarUltimoRegistro();
    service.analizarUltimoRegistro();

    verify(notificationService, times(1)).enviarAlerta(registroCritico);
  }

  @Test
  void analizaYAlertaDeNuevoCuandoLlegaUnRegistroConIdDistinto() {
    service = new AlertaAnalisisServiceImpl(
        repository, notificationService, UMBRAL_TEMPERATURA, UMBRAL_HUMEDAD);
    RegistroClimatico primero = crearRegistro(40.0, 70);
    primero.setId(1L);
    RegistroClimatico segundo = crearRegistro(41.0, 71);
    segundo.setId(2L);

    when(repository.obtenerUltimo())
        .thenReturn(Optional.of(primero))
        .thenReturn(Optional.of(segundo));

    service.analizarUltimoRegistro();
    service.analizarUltimoRegistro();

    verify(notificationService).enviarAlerta(primero);
    verify(notificationService).enviarAlerta(segundo);
  }
}
