package ar.edu.utn.frba.ddsi.climalert.scheduler;

import ar.edu.utn.frba.ddsi.climalert.services.ClimaPollingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ClimaPollingScheduler {

  private final ClimaPollingService climaPollingService;

  public ClimaPollingScheduler(ClimaPollingService climaPollingService) {
    this.climaPollingService = climaPollingService;
  }

  @Scheduled(fixedRate = 300000)
  public void ejecutarPolling() {
    climaPollingService.registrarClimaActual();
  }
}
