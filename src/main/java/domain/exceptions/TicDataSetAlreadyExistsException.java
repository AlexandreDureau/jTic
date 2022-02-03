package domain.exceptions;

public class TicDataSetAlreadyExistsException extends TicException{

    public TicDataSetAlreadyExistsException(String label) {
        this.message = "DataSet <" + label + "> already exists";
    }

    public TicDataSetAlreadyExistsException(String label, int index) {
        this.message = "DataSet " + label + " already exists at index " + index;
    }
}
