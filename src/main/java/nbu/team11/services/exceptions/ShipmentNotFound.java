package nbu.team11.services.exceptions;


public class ShipmentNotFound extends RuntimeException {
    public ShipmentNotFound(String message) {
        super(message);
    }
}