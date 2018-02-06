package services.exceptions;

public class SharedDirectoryIsEmptyException extends RuntimeException{
    public SharedDirectoryIsEmptyException(String iva_errMsg){
        super(iva_errMsg);
    }
}
