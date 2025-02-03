package nbu.team11.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import nbu.team11.entities.enums.Role;

public class UserTest {

  private List<String> validate(User user) {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    return validator.validate(user)
        .stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());
  }

  @Test
  void whenDataIsValid() {
    User user = new User(Role.CLIENT, "test@mail.com", "password", "username", "firstName", "lastName");

    List<String> messages = validate(user);

    assertEquals(0, messages.size());
  }

  @Test
  void whenFistNameIsBlank() {
    User user = new User(Role.CLIENT, "test@mail.com", "password", "username", "", "lastName");

    List<String> messages = validate(user);

    assertEquals(1, messages.size());
    assertEquals(messages.get(0), "First Name cannot be blank!");
  }

  @Test
  void whenUsernameIsBlank() {
    User user = new User(Role.CLIENT, "test@mail.com", "password", "", "FirstName", "lastName");

    List<String> messages = validate(user);

    assertEquals(2, messages.size());
    assertEquals(messages.get(0), "Username cannot be blank!");
  }

  @Test
  void whenLastNameIsBlank() {
    User user = new User(Role.CLIENT, "test@mail.com", "password", "Username", "FirstName", "");

    List<String> messages = validate(user);

    assertEquals(1, messages.size());
    assertEquals(messages.get(0), "Last Name cannot be blank!");
  }

  @Test
  void whenPasswordIsBlank() {
    User user = new User(Role.CLIENT, "test@mail.com", "", "Username", "FirstName", "lastName");

    List<String> messages = validate(user);

    assertEquals(1, messages.size());
    assertEquals(messages.get(0), "Password cannot be blank!");
  }

  @Test
  void whenEmailIsBlank() {
    User user = new User(Role.CLIENT, "", "password", "Username", "FirstName", "lastName");

    List<String> messages = validate(user);

    assertEquals(1, messages.size());
    assertEquals(messages.get(0), "Email cannot be blank!");
  }

  @Test
  void whenEmailIsInvalid() {
    User user = new User(Role.CLIENT, "email.test.com", "password", "Username", "FirstName", "lastName");

    List<String> messages = validate(user);

    assertEquals(1, messages.size());
    assertEquals(messages.get(0), "Invalid email address. Please enter a proper email!");
  }

  @Test
  void testGetEmail() {
    String email = "email@test.com";
    User user = new User(Role.CLIENT, email, "password", "Username", "FirstName", "lastName");
    assertEquals(user.getEmail(), email);
  }

  @Test
  void testGetFirstName() {
    String firstName = "FirstName";
    User user = new User(Role.CLIENT, "email@mail.com", "password", "Username", firstName, "lastName");
    assertEquals(user.getFirstName(), firstName);
  }

  @Test
  void testGetFullName() {
    String firstName = "FirstName";
    String lastName = "LastName";
    User user = new User(Role.CLIENT, "email@test.com", "password", "Username", firstName, lastName);
    String fullName = firstName + " " + lastName;
    assertEquals(user.getFullName(), fullName);
  }
}
