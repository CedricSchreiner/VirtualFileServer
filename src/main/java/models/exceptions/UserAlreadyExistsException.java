package models.exceptions;

public class UserAlreadyExistsException extends UserException {
    public UserAlreadyExistsException(String iva_message) {
        super(iva_message);
    }
}
