package nbu.team11.services.exceptions;

public class ResourceNotFound extends Exception {

    public ResourceNotFound() {
        super("User not found.");
    }
}
