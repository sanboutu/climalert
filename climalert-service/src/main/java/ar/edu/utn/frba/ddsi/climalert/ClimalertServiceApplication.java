package ar.edu.utn.frba.ddsi.climalert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ClimalertServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(ClimalertServiceApplication.class, args);
  }
}
