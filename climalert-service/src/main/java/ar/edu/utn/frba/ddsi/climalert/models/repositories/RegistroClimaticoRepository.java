package ar.edu.utn.frba.ddsi.climalert.models.repositories;

import ar.edu.utn.frba.ddsi.climalert.models.entities.RegistroClimatico;
import java.util.List;
import java.util.Optional;

public interface RegistroClimaticoRepository {

  void guardar(RegistroClimatico registro);

  Optional<RegistroClimatico> obtenerUltimo();

  List<RegistroClimatico> obtenerTodos();
}
