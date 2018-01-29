package services.exceptions;

public class AdminAlreadyExistsException extends RuntimeException {
    public AdminAlreadyExistsException(String iob_errMsg) {
        super(iob_errMsg);
    }
}
