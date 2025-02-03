package nbu.team11.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class CountryTest {

  private List<String> validate(Country country) {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    return validator.validate(country)
        .stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());
  }

  @Test
  void whenDataIsValid() {
    Country country = new Country("Country1");

    List<String> messages = validate(country);

    assertEquals(0, messages.size());
  }

  @Test
  void whenNameIsBlank() {
    Country country = new Country("");

    List<String> messages = validate(country);

    assertEquals(2, messages.size());
    assertEquals(messages.get(1), "Country name cannot be blank!");
  }

  @Test
  void whenNameIsLessThan5Symbols() {
    Country country = new Country("coun");

    List<String> messages = validate(country);

    assertEquals(1, messages.size());
    assertEquals(messages.get(0), "Country name has to be between 5 and 10 characters!");
  }
}
