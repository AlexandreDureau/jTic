package domain.exceptions;

public class TicChecksumException extends TicException{

    public TicChecksumException(){}

    public TicChecksumException(String message){
        super();
        this.message = message;
    }

}
