package common.exceptions;

public class TicInvalidFormatException extends Exception{


    protected String message;
    public TicInvalidFormatException(String text, int index) {
        this.message = "Tic element '"+ text + "' is invalid at char[" + index + "]";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
