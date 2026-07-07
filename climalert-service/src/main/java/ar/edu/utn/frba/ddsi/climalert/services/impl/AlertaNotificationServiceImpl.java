package ar.edu.utn.frba.ddsi.climalert.services.impl;

import ar.edu.utn.frba.ddsi.climalert.models.entities.RegistroClimatico;
import ar.edu.utn.frba.ddsi.climalert.services.AlertaNotificationService;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AlertaNotificationServiceImpl implements AlertaNotificationService {

  private static final DateTimeFormatter FORMATO_FECHA =
      DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

  private final JavaMailSender mailSender;
  private final String[] destinatarios;

  public AlertaNotificationServiceImpl(
      JavaMailSender mailSender,
      @Value("${climalert.alerta.destinatarios}") String[] destinatarios) {
    this.mailSender = mailSender;
    this.destinatarios = destinatarios.clone();
  }

  @Override
  public void enviarAlerta(RegistroClimatico registro) {
    SimpleMailMessage mensaje = new SimpleMailMessage();
    mensaje.setTo(destinatarios);
    mensaje.setSubject("Alerta climática - " + registro.getUbicacion());
    mensaje.setText(construirCuerpo(registro));

    mailSender.send(mensaje);

    log.info("Mail de alerta enviado a {} - asunto: '{}'",
        String.join(", ", destinatarios), mensaje.getSubject());
  }

  private String construirCuerpo(RegistroClimatico registro) {
    return "Se ha detectado una condición climática crítica.\n\n"
        + "Ubicación: " + registro.getUbicacion() + "\n"
        + "Fecha y hora: " + registro.getFechaHora().format(FORMATO_FECHA) + "\n"
        + "Temperatura: " + registro.getTemperatura() + "°C\n"
        + "Humedad: " + registro.getHumedad() + "%\n"
        + "Condición: " + registro.getCondicion();
  }
}