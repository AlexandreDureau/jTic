package domain.exceptions;

public abstract class TicException extends Exception{

    protected String message;

    @Override
    public String getMessage() {

        return message;
    }
}
