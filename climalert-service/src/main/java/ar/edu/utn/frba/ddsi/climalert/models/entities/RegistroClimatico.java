package ar.edu.utn.frba.ddsi.climalert.models.entities;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RegistroClimatico {
  private Long id;
  private final LocalDateTime fechaHora;
  private final String ubicacion;
  private final double temperatura;
  private final double sensacionTermica;
  private final int humedad;
  private final String condicion;
  private final double velocidadViento;
  private final String direccionViento;
  private final double presion;
  private final double indiceUv;
  private final int nubosidad;
  private final double precipitacion;

  public boolean esCondicionCritica(double umbralTemperatura, int umbralHumedad) {
    return temperatura > umbralTemperatura && humedad > umbralHumedad;
  }
}