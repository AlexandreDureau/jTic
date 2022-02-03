package domain.exceptions;

public class TicInvalidFormatException extends TicException{

    public TicInvalidFormatException(String message) {
        this.message = message;
    }

    public TicInvalidFormatException(String text, int index) {
        this.message = "Tic element '"+ text + "' is invalid at char[" + index + "]";
    }
}
