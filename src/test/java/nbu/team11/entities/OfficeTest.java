package nbu.team11.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class OfficeTest {

  private List<String> validate(Office office) {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    return validator.validate(office)
        .stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());
  }

  @Test
  void whenDataIsValid() {
    Office office = new Office("Office1");

    List<String> messages = validate(office);

    assertEquals(0, messages.size());
  }

  @Test
  void whenTitleIsBlank() {
    Office office = new Office("");

    List<String> messages = validate(office);

    assertEquals(2, messages.size());
    assertEquals(messages.get(1), "Title cannot be blank!");
  }

  @Test
  void whenTitleIsLessThan5Symbols() {
    Office office = new Office("of");

    List<String> messages = validate(office);

    assertEquals(1, messages.size());
    assertEquals(messages.get(0), "Title has to be between 5 and 20 characters!");
  }

}
