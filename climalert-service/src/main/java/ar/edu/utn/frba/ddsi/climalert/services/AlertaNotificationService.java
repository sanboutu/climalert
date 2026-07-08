package ar.edu.utn.frba.ddsi.climalert.services;

import ar.edu.utn.frba.ddsi.climalert.models.entities.RegistroClimatico;

public interface AlertaNotificationService {

  void enviarAlerta(RegistroClimatico registro);
}
