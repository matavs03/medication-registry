package rs.ac.bg.fon.medicationregistry.exceptions;

public class NonValidAdminDeactivationException extends RuntimeException {
    public NonValidAdminDeactivationException(String message) {
        super(message);
    }
}
