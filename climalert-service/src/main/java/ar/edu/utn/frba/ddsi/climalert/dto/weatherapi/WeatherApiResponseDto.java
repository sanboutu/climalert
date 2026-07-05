package ar.edu.utn.frba.ddsi.climalert.dto.weatherapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WeatherApiResponseDto(Location location, Current current) {

  public record Location(String name) {
  }

  public record Current(
      @JsonProperty("temp_c") double tempC,
      int humidity,
      Condition condition,
      @JsonProperty("last_updated") String lastUpdated) {
  }

  public record Condition(String text) {
  }
}