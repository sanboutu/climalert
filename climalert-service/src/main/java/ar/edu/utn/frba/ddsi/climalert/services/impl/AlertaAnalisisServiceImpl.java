package ar.edu.utn.frba.ddsi.climalert.services.impl;

import ar.edu.utn.frba.ddsi.climalert.models.entities.RegistroClimatico;
import ar.edu.utn.frba.ddsi.climalert.models.repositories.RegistroClimaticoRepository;
import ar.edu.utn.frba.ddsi.climalert.services.AlertaAnalisisService;
import ar.edu.utn.frba.ddsi.climalert.services.AlertaNotificationService;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
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

    if (ultimoRegistro.isEmpty()) {
      log.info("Analisis de alerta: no hay registros disponibles todavia.");
      return;
    }

    RegistroClimatico registro = ultimoRegistro.get();
    boolean esCritico = registro.esCondicionCritica(umbralTemperatura, umbralHumedad);

    log.info(
        "Analisis de alerta - ubicacion: {}, temperatura: {}°C (umbral: {}°C), "
            + "humedad: {}% (umbral: {}%), resultado: {}",
        registro.getUbicacion(),
        registro.getTemperatura(),
        umbralTemperatura,
        registro.getHumedad(),
        umbralHumedad,
        esCritico ? "ALERTA" : "sin alerta");

    if (esCritico) {
      notificationService.enviarAlerta(registro);
    }
  }
}