package ar.edu.utn.frba.ddsi.climalert.dto.weatherapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WeatherApiResponseDto(Location location, Current current) {

  public record Location(String name) {
  }

  public record Current(
      @JsonProperty("temp_c") double tempC,
      @JsonProperty("feelslike_c") double feelslikeC,
      int humidity,
      Condition condition,
      @JsonProperty("wind_kph") double windKph,
      @JsonProperty("wind_dir") String windDir,
      @JsonProperty("pressure_mb") double pressureMb,
      double uv,
      int cloud,
      @JsonProperty("precip_mm") double precipMm,
      @JsonProperty("last_updated") String lastUpdated) {
  }

  public record Condition(String text) {
  }
}