package ar.edu.utn.frba.ddsi.climalert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import ar.edu.utn.frba.ddsi.climalert.models.entities.RegistroClimatico;
import java.time.LocalDateTime;

import ar.edu.utn.frba.ddsi.climalert.services.impl.AlertaNotificationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class AlertaNotificationServiceImplTest {

  private static final String[] DESTINATARIOS = {
      "admin@clima.com", "emergencias@clima.com", "meteorologia@clima.com"
  };

  @Mock
  private JavaMailSender mailSender;

  private AlertaNotificationServiceImpl service;

  @Test
  void enviaMailATodosLosDestinatariosConElDetalleDelClima() {
    service = new AlertaNotificationServiceImpl(mailSender, DESTINATARIOS);
    RegistroClimatico registro = RegistroClimatico.builder()
        .fechaHora(LocalDateTime.of(2026, 7, 3, 18, 30))
        .ubicacion("Ciudad Evita")
        .temperatura(36.5)
        .humedad(65)
        .condicion("Clear")
        .build();

    service.enviarAlerta(registro);

    ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
    verify(mailSender).send(captor.capture());

    SimpleMailMessage mensajeEnviado = captor.getValue();
    assertThat(mensajeEnviado.getTo()).containsExactly(DESTINATARIOS);
    assertThat(mensajeEnviado.getSubject()).contains("Ciudad Evita");
    assertThat(mensajeEnviado.getText())
        .contains("Ciudad Evita")
        .contains("36.5")
        .contains("65")
        .contains("Clear");
  }
}
