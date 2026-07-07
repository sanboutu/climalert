package ar.edu.utn.frba.ddsi.climalert.scheduler;

import ar.edu.utn.frba.ddsi.climalert.services.AlertaAnalisisService;
import ar.edu.utn.frba.ddsi.climalert.services.ClimaPollingService;
import java.time.LocalDateTime;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ClimalertScheduler {

  private static final int INTERVALO_POLLING_MINUTOS = 5;

  private final ClimaPollingService climaPollingService;
  private final AlertaAnalisisService alertaAnalisisService;

  public ClimalertScheduler(
      ClimaPollingService climaPollingService,
      AlertaAnalisisService alertaAnalisisService) {
    this.climaPollingService = climaPollingService;
    this.alertaAnalisisService = alertaAnalisisService;
  }

  @Scheduled(cron = "0 * * * * *")
  public void ejecutarCiclo() {
    int minutoActual = LocalDateTime.now().getMinute();

    if (minutoActual % INTERVALO_POLLING_MINUTOS == 0) {
      climaPollingService.registrarClimaActual();
    }

    alertaAnalisisService.analizarUltimoRegistro();
  }
}