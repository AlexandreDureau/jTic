package domain.ticframe;

import domain.exceptions.TicChecksumException;
import domain.exceptions.TicInvalidFormatException;
import domain.ticdataset.TicDataSet;

import java.time.LocalDateTime;

public interface TicFrame {

    void clear();
    TicDataSet addDataSet();
    TicDataSet addDataSet(String label);
    TicDataSet addDataSet(String label, String value) throws TicInvalidFormatException;
    TicDataSet addDataSet(String label, String value, LocalDateTime timestamp) throws TicChecksumException, TicInvalidFormatException;
    TicDataSet addDataSet(String label, String value, LocalDateTime timestamp, Byte checksum) throws TicChecksumException, TicInvalidFormatException;
    TicDataSet addDataSet(int index, String label);
    TicDataSet addDataSet(int index, String label, String value) throws TicInvalidFormatException;
    TicDataSet addDataSet(int index, String label, String value, LocalDateTime timestamp) throws TicChecksumException, TicInvalidFormatException;
    TicDataSet addDataSet(int index, String label, String value, LocalDateTime timestamp, Byte checksum) throws TicChecksumException, TicInvalidFormatException;
    TicDataSet getDataSet(String label);
    byte[] getBytes();
}
