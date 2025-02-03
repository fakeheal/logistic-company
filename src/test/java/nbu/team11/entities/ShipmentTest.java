package nbu.team11.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import nbu.team11.entities.enums.PositionType;
import nbu.team11.entities.enums.Role;

public class ShipmentTest {

  private Employee employee;
  private Client client;
  private Office office;
  private User user;
  private Address address;

  private List<String> validate(Shipment shipment) {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    return validator.validate(shipment)
        .stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());
  }

  @BeforeEach
  void init() {
    user = new User(Role.CLIENT, "test@mail.com", "password", "username", "firstName", "lastName");
    client = new Client(user, "0888888888");
    office = new Office("Office1");
    employee = new Employee(user, office, PositionType.ADMIN);
    address = new Address("Street 1", "BG1234");
  }

  @Test
  void whenDataIsValid() {
    Shipment shipment = new Shipment(employee, client, address, client, address, office, 12.5, BigDecimal.valueOf(30));

    List<String> messages = validate(shipment);

    assertEquals(0, messages.size());
  }

  @Test
  void whenPriceIsNotPositive() {
    Shipment shipment = new Shipment(employee, client, address, client, address, office, 12.5, BigDecimal.valueOf(0));

    List<String> messages = validate(shipment);

    assertEquals(1, messages.size());
    assertEquals(messages.get(0), "The price must be greater than 0");
  }

  @Test
  void whenWeightIsNotPositive() {
    Shipment shipment = new Shipment(employee, client, address, client, address, office, 0, BigDecimal.valueOf(0));

    List<String> messages = validate(shipment);

    assertEquals(1, messages.size());
    assertEquals(messages.get(0), "The price must be greater than 0");
  }
}
