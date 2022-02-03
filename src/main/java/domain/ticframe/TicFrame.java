package domain.ticframe;

import arrays.ByteArray;
import domain.exceptions.TicDataSetAlreadyExistsException;
import domain.exceptions.TicDataSetNotFoundException;
import domain.exceptions.TicDataSetUnexpectedTypeException;
import domain.ticdataset.TicDataSet;
import domain.ticdataset.TicHistoDataSet;

import java.util.List;

public interface TicFrame {

    List<?extends TicDataSet> getDataSetList();
    <T extends TicDataSet> T getDataSet(String label);
    <T extends TicDataSet> void addDataSet(T dataSet) throws TicDataSetAlreadyExistsException, TicDataSetUnexpectedTypeException;
    <T extends TicDataSet> void addDataSet(int index,T dataSet) throws TicDataSetAlreadyExistsException, TicDataSetUnexpectedTypeException;
    void moveDataSet(String label, int index) throws TicDataSetNotFoundException;
    void clear();
    int indexOf(String label);
    void removeDataSet(String label);
    void removeDataSetAt(int index);
    String toString();
    String toText();
    byte[] getBytes();
}
