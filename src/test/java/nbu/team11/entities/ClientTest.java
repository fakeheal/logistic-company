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

public class ClientTest {

  private User user;

  private List<String> validate(Client client) {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    return validator.validate(client)
        .stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());
  }

  @BeforeEach
  void init() {
    user = new User(Role.CLIENT, "test@mail.com", "password", "username", "firstName", "lastName");
  }

  @Test
  void whenDataIsValid() {
    Client client = new Client(user, "0888888888");

    List<String> messages = validate(client);

    assertEquals(0, messages.size());
  }

  @Test
  void whenPhoneNumberIsInvalid() {
    Client client = new Client(user, "08");

    List<String> messages = validate(client);

    assertEquals(1, messages.size());
    assertEquals(messages.get(0), "Phone number has to be exactly 10 characters!");
  }

}
