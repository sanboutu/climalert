package ar.edu.utn.frba.ddsi.climalert.models.repositories.impl;

import ar.edu.utn.frba.ddsi.climalert.models.entities.RegistroClimatico;
import ar.edu.utn.frba.ddsi.climalert.models.repositories.RegistroClimaticoRepository;
import ar.edu.utn.frba.ddsi.climalert.utils.GeneradorIdSecuencial;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryRegistroClimaticoRepository implements RegistroClimaticoRepository {

  private final List<RegistroClimatico> registros = new CopyOnWriteArrayList<>();
  private final GeneradorIdSecuencial generadorId = new GeneradorIdSecuencial();

  @Override
  public void guardar(RegistroClimatico registro) {
    registro.setId(generadorId.siguiente());
    registros.add(registro);
  }

  @Override
  public Optional<RegistroClimatico> obtenerUltimo() {
    if (registros.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(registros.getLast());
  }

  @Override
  public List<RegistroClimatico> obtenerTodos() {
    return Collections.unmodifiableList(registros);
  }
}
