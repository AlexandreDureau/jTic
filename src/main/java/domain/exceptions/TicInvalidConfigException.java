package domain.exceptions;

public class TicInvalidConfigException extends TicException{

    public TicInvalidConfigException(String message) {
        this.message = message;
    }

    public TicInvalidConfigException(String message, Throwable cause) {
        this.message = message + " cause : " + cause.getMessage();
    }
}
