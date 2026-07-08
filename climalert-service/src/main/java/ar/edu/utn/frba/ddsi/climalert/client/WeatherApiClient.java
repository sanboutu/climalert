package ar.edu.utn.frba.ddsi.climalert.client;

import ar.edu.utn.frba.ddsi.climalert.dto.weatherapi.WeatherApiResponseDto;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WeatherApiClient {

  private final RestTemplate restTemplate;
  private final String baseUrl;
  private final String apiKey;
  private final String location;

  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP2",
      justification = "RestTemplate es un bean singleton de Spring inyectado por DI, "
          + "no un dato de negocio mutable externamente.")
  public WeatherApiClient(
      RestTemplate restTemplate,
      @Value("${weatherapi.base-url}") String baseUrl,
      @Value("${weatherapi.api-key}") String apiKey,
      @Value("${weatherapi.location}") String location) {
    this.restTemplate = restTemplate;
    this.baseUrl = baseUrl;
    this.apiKey = apiKey;
    this.location = location;
  }

  public WeatherApiResponseDto obtenerClimaActual() {
    String url = String.format(
        "%s/current.json?key=%s&q=%s", baseUrl, apiKey, location);
    return restTemplate.getForObject(url, WeatherApiResponseDto.class);
  }
}
