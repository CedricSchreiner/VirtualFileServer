package models.exceptions;

public class UserException extends RuntimeException {

    public UserException(String iva_message) {
        super(iva_message);
    }
}
