package ar.edu.utn.frba.ddsi.climalert.utils;

import java.util.concurrent.atomic.AtomicLong;

public class GeneradorIdSecuencial {

  private final AtomicLong siguiente;

  public GeneradorIdSecuencial() {
    this(1L);
  }

  public GeneradorIdSecuencial(long valorInicial) {
    this.siguiente = new AtomicLong(valorInicial);
  }

  public long siguiente() {
    return siguiente.getAndIncrement();
  }
}
