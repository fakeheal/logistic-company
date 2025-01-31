package nbu.team11.services.exceptions;


public class CountryNotFound extends RuntimeException {
    public CountryNotFound(String message) {
        super(message);
    }
}
