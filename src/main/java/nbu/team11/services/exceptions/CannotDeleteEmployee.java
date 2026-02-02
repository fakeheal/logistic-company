package nbu.team11.services.exceptions;

public class CannotDeleteEmployee extends Exception {
    public CannotDeleteEmployee(String message) {
        super(message);
    }
}
