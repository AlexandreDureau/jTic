package domain.exceptions;

import static domain.TicTimestamp.FORMAT;

public class TicInvalidTimestampException extends TicException{

    public TicInvalidTimestampException(String timestamp) {
        this.message = "Timestamp '"+ timestamp + "' is not valid : format should be " + FORMAT;
    }

    public TicInvalidTimestampException(String timestamp, Throwable cause) {
        this.message = "Timestamp '"+ timestamp + "' is not valid ( " + cause.getMessage() + "" ;
    }

}
