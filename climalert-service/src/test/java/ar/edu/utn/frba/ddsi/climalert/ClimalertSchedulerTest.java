package ar.edu.utn.frba.ddsi.climalert;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import ar.edu.utn.frba.ddsi.climalert.scheduler.ClimalertScheduler;
import ar.edu.utn.frba.ddsi.climalert.services.AlertaAnalisisService;
import ar.edu.utn.frba.ddsi.climalert.services.ClimaPollingService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClimalertSchedulerTest {

  @Mock
  private ClimaPollingService climaPollingService;

  @Mock
  private AlertaAnalisisService alertaAnalisisService;

  private ClimalertScheduler scheduler;

  @Test
  void enMinutoMultiploDeCincoPolleaAntesDeAnalizar() {
    scheduler = new ClimalertScheduler(climaPollingService, alertaAnalisisService);
    LocalDateTime minutoMultiploDeCinco = LocalDateTime.of(2026, 7, 7, 18, 20, 0);

    try (MockedStatic<LocalDateTime> mocked = mockStatic(LocalDateTime.class)) {
      mocked.when(LocalDateTime::now).thenReturn(minutoMultiploDeCinco);

      scheduler.ejecutarCiclo();
    }

    InOrder orden = Mockito.inOrder(climaPollingService, alertaAnalisisService);
    orden.verify(climaPollingService).registrarClimaActual();
    orden.verify(alertaAnalisisService).analizarUltimoRegistro();
  }

  @Test
  void enMinutoNoMultiploDeCincoNoPolleaSoloAnaliza() {
    scheduler = new ClimalertScheduler(climaPollingService, alertaAnalisisService);
    LocalDateTime minutoNoMultiplo = LocalDateTime.of(2026, 7, 7, 18, 21, 0);

    try (MockedStatic<LocalDateTime> mocked = mockStatic(LocalDateTime.class)) {
      mocked.when(LocalDateTime::now).thenReturn(minutoNoMultiplo);

      scheduler.ejecutarCiclo();
    }

    verify(climaPollingService, never()).registrarClimaActual();
    verify(alertaAnalisisService).analizarUltimoRegistro();
  }
}
