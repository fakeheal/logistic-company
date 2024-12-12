package nbu.team11.services.exceptions;

public class UsernameNotAvailable extends Exception {

    public UsernameNotAvailable() {
        super("Username is already taken.");
    }
}
