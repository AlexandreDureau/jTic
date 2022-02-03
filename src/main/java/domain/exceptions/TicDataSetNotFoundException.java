package domain.exceptions;

public class TicDataSetNotFoundException  extends TicException {

    public TicDataSetNotFoundException(String label) {
        this.message = "DataSet <" + label + "> not found";
    }
}