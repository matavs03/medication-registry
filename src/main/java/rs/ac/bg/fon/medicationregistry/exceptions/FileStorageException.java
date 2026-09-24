package rs.ac.bg.fon.medicationregistry.exceptions;

public class FileStorageException extends RuntimeException {
    public FileStorageException(String message) {
        super(message);
    }
    public FileStorageException(String message,  Throwable cause) {
        super(message);
    }
}
