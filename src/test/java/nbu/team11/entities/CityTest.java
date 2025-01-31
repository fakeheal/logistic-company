package nbu.team11.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import nbu.team11.entities.enums.Role;

public class CityTest {

  private Country country;

  private List<String> validate(City city) {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    return validator.validate(city)
        .stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());
  }

  @BeforeEach
  void init() {
    country = new Country("Country1");

  }

  @Test
  void whenDataIsValid() {
    City address = new City("City 1", country);

    List<String> messages = validate(address);

    assertEquals(0, messages.size());
  }

  @Test
  void whenNameIsBlank() {
    City address = new City("", country);

    List<String> messages = validate(address);

    assertEquals(2, messages.size());
    assertEquals(messages.get(0), "City name cannot be blank!");
  }

  @Test
  void whenNameIsLessThan3Symbols() {
    City address = new City("c", country);

    List<String> messages = validate(address);

    assertEquals(1, messages.size());
    assertEquals(messages.get(0), "City name has to be between 3 and 20 characters!");
  }

}
