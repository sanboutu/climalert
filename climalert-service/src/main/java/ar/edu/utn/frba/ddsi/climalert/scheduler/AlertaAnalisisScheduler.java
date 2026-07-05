package ar.edu.utn.frba.ddsi.climalert.scheduler;

import ar.edu.utn.frba.ddsi.climalert.services.AlertaAnalisisService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AlertaAnalisisScheduler {

  private final AlertaAnalisisService alertaAnalisisService;

  public AlertaAnalisisScheduler(AlertaAnalisisService alertaAnalisisService) {
    this.alertaAnalisisService = alertaAnalisisService;
  }

  @Scheduled(fixedRate = 60000)
  public void ejecutarAnalisis() {
    alertaAnalisisService.analizarUltimoRegistro();
  }
}
