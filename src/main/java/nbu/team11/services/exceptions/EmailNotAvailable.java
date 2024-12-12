package nbu.team11.services.exceptions;

public class EmailNotAvailable extends Exception {

    public EmailNotAvailable() {
        super("Email is already taken.");
    }
}
