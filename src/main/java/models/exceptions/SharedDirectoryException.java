package models.exceptions;

public class SharedDirectoryException extends RuntimeException {
    public SharedDirectoryException(String iva_msg) {
        super(iva_msg);
    }
}
