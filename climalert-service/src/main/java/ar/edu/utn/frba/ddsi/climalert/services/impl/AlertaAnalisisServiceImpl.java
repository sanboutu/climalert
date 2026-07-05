package ar.edu.utn.frba.ddsi.climalert.services.impl;

import ar.edu.utn.frba.ddsi.climalert.models.entities.RegistroClimatico;
import ar.edu.utn.frba.ddsi.climalert.models.repositories.RegistroClimaticoRepository;
import ar.edu.utn.frba.ddsi.climalert.services.AlertaAnalisisService;
import ar.edu.utn.frba.ddsi.climalert.services.AlertaNotificationService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AlertaAnalisisServiceImpl implements AlertaAnalisisService {

  private final RegistroClimaticoRepository repository;
  private final AlertaNotificationService notificationService;
  private final double umbralTemperatura;
  private final int umbralHumedad;

  public AlertaAnalisisServiceImpl(
      RegistroClimaticoRepository repository,
      AlertaNotificationService notificationService,
      @Value("${climalert.alerta.temperatura-umbral}") double umbralTemperatura,
      @Value("${climalert.alerta.humedad-umbral}") int umbralHumedad) {
    this.repository = repository;
    this.notificationService = notificationService;
    this.umbralTemperatura = umbralTemperatura;
    this.umbralHumedad = umbralHumedad;
  }

  @Override
  public void analizarUltimoRegistro() {
    Optional<RegistroClimatico> ultimoRegistro = repository.obtenerUltimo();

    ultimoRegistro
        .filter(registro -> registro.esCondicionCritica(umbralTemperatura, umbralHumedad))
        .ifPresent(notificationService::enviarAlerta);
  }
}
