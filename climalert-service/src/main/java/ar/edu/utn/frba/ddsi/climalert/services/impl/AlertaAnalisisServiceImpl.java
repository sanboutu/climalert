package ar.edu.utn.frba.ddsi.climalert.services.impl;

import ar.edu.utn.frba.ddsi.climalert.models.entities.RegistroClimatico;
import ar.edu.utn.frba.ddsi.climalert.models.repositories.RegistroClimaticoRepository;
import ar.edu.utn.frba.ddsi.climalert.services.AlertaAnalisisService;
import ar.edu.utn.frba.ddsi.climalert.services.AlertaNotificationService;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
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
  private final AtomicReference<Long> ultimoIdAnalizado = new AtomicReference<>();

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

    if (registro.getId().equals(ultimoIdAnalizado.get())) {
      log.info(
          "Analisis de alerta: el ultimo registro (id={}) ya fue analizado, se omite.",
          registro.getId());
      return;
    }

    ultimoIdAnalizado.set(registro.getId());
    boolean esCritico = registro.esCondicionCritica(umbralTemperatura, umbralHumedad);

    log.info(
        "Analisis de alerta - id: {}, ubicacion: {}, temperatura: {}°C (umbral: {}°C), "
            + "humedad: {}% (umbral: {}%), resultado: {}",
        registro.getId(),
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