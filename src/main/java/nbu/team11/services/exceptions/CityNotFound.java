package nbu.team11.services.exceptions;


public class CityNotFound extends RuntimeException {
    public CityNotFound(String message) {
        super(message);
    }
}
