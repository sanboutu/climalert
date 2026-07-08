package ar.edu.utn.frba.ddsi.climalert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ar.edu.utn.frba.ddsi.climalert.models.entities.RegistroClimatico;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import ar.edu.utn.frba.ddsi.climalert.models.repositories.impl.InMemoryRegistroClimaticoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class InMemoryRegistroClimaticoRepositoryTest {

  private InMemoryRegistroClimaticoRepository repository;

  @BeforeEach
  void setUp() {
    repository = new InMemoryRegistroClimaticoRepository();
  }

  private RegistroClimatico crearRegistro(String ubicacion) {
    return RegistroClimatico.builder()
        .fechaHora(LocalDateTime.now())
        .ubicacion(ubicacion)
        .temperatura(20.0)
        .humedad(50)
        .condicion("Clear")
        .build();
  }

  @Test
  void obtenerUltimoDevuelveVacioSinRegistros() {
    Optional<RegistroClimatico> resultado = repository.obtenerUltimo();

    assertThat(resultado).isEmpty();
  }

  @Test
  void guardarAsignaIdAlRegistro() {
    RegistroClimatico registro = crearRegistro("Buenos Aires");

    repository.guardar(registro);

    assertThat(registro.getId()).isNotNull();
  }

  @Test
  void obtenerUltimoDevuelveElUltimoRegistroGuardado() {
    repository.guardar(crearRegistro("Buenos Aires"));
    RegistroClimatico ultimo = crearRegistro("Ciudad Evita");
    repository.guardar(ultimo);

    Optional<RegistroClimatico> resultado = repository.obtenerUltimo();

    assertThat(resultado).contains(ultimo);
  }

  @Test
  void obtenerTodosDevuelveTodosLosRegistrosEnOrden() {
    RegistroClimatico primero = crearRegistro("Buenos Aires");
    RegistroClimatico segundo = crearRegistro("Ciudad Evita");
    repository.guardar(primero);
    repository.guardar(segundo);

    List<RegistroClimatico> resultado = repository.obtenerTodos();

    assertThat(resultado).containsExactly(primero, segundo);
  }

  @Test
  void obtenerTodosDevuelveListaInmutable() {
    repository.guardar(crearRegistro("Buenos Aires"));
    List<RegistroClimatico> resultado = repository.obtenerTodos();

    assertThatThrownBy(() -> resultado.add(crearRegistro("Otra")))
        .isInstanceOf(UnsupportedOperationException.class);
  }
}
