package domain.exceptions;

public class TicDataSetUnexpectedTypeException extends TicException{

    public TicDataSetUnexpectedTypeException(Class expected, Class actual ) {
        this.message = "Invalid DataSet type. Expected <" + expected + "> , Actual <"+  actual + ">";
    }
}
