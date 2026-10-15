package rs.ac.bg.fon.medicationregistry.exceptions;

public class NonValidPasswordException extends RuntimeException {
    public NonValidPasswordException(String message) {
        super(message);
    }
}
