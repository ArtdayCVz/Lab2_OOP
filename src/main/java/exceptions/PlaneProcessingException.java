package exceptions;

public class PlaneProcessingException extends RuntimeException {
    public PlaneProcessingException(String message) {
        super(message);
    }

    public PlaneProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
