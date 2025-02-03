package nbu.team11.entities;

import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.util.List;
import org.junit.jupiter.api.Test;

public class AddressTest {

  private List<String> validate(Address address) {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    return validator.validate(address)
        .stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());
  }

  @Test
  void whenDataIsValid() {
    Address address = new Address("Street 1", "BG1234");

    List<String> messages = validate(address);

    assertEquals(0, messages.size());
  }

  @Test
  void whenStreetIsEmpty() {
    Address address = new Address("", "BG1234");

    List<String> messages = validate(address);

    assertEquals(2, messages.size());
    assertEquals(messages.get(0), "Street cannot be blank!");
  }

  @Test
  void whenStreetIsLessThanFiveCharaters() {
    Address address = new Address("str", "BG1234");

    List<String> messages = validate(address);

    assertEquals(1, messages.size());
    assertEquals(messages.get(0), "Street name has to be between 5 and 20 characters!");
  }

  @Test
  void whenPostalCodeIsBlank() {
    Address address = new Address("Street 1", "");

    List<String> messages = validate(address);

    assertEquals(1, messages.size());
    assertEquals(messages.get(0), "Postal code cannot be blank!");
  }

}
