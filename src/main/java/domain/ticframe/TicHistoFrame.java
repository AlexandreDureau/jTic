package domain.ticframe;

import arrays.ByteArray;
import domain.exceptions.TicChecksumException;
import domain.exceptions.TicInvalidFormatException;
import domain.ticdataset.TicDataSet;
import domain.ticdataset.TicHistoDataSet;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TicHistoFrame implements TicFrame{

    protected List<TicHistoDataSet> dataSetList = new ArrayList<TicHistoDataSet>();

    @Override
    public void clear() {
        dataSetList.clear();
    }


    @Override
    public TicDataSet addDataSet() {
        TicHistoDataSet dataSet = new TicHistoDataSet();
        dataSetList.add(dataSet);
        return dataSet;
    }


    @Override
    public TicDataSet addDataSet(String label) {
        TicHistoDataSet dataSet = new TicHistoDataSet(label);
        dataSetList.add(dataSet);
        return dataSet;
    }


    @Override
    public TicDataSet addDataSet(String label, String value) throws TicInvalidFormatException {
        TicHistoDataSet dataSet = new TicHistoDataSet(label, value);
        dataSetList.add(dataSet);
        return dataSet;
    }


    @Override
    public TicDataSet addDataSet(String label, String value, LocalDateTime timestamp) throws TicChecksumException, TicInvalidFormatException {
        TicHistoDataSet dataSet = new TicHistoDataSet(label, value, timestamp);
        dataSetList.add(dataSet);
        return dataSet;
    }


    @Override
    public TicDataSet addDataSet(String label, String value, LocalDateTime timestamp, Byte checksum) throws TicChecksumException, TicInvalidFormatException {
        TicHistoDataSet dataSet = new TicHistoDataSet(label, value, timestamp, checksum);
        dataSetList.add(dataSet);
        return dataSet;
    }


    @Override
    public TicDataSet addDataSet(int index, String label) {
        TicHistoDataSet dataSet = new TicHistoDataSet(label);
        dataSetList.add(index, dataSet);
        return dataSet;
    }


    @Override
    public TicDataSet addDataSet(int index, String label, String value) throws TicInvalidFormatException {
        TicHistoDataSet dataSet = new TicHistoDataSet(label, value);
        dataSetList.add(index, dataSet);
        return dataSet;
    }


    @Override
    public TicDataSet addDataSet(int index, String label, String value, LocalDateTime timestamp) throws TicChecksumException, TicInvalidFormatException {
        TicHistoDataSet dataSet = new TicHistoDataSet(label, value, timestamp);
        dataSetList.add(index, dataSet);
        return dataSet;
    }


    @Override
    public TicDataSet addDataSet(int index, String label, String value, LocalDateTime timestamp, Byte checksum) throws TicChecksumException, TicInvalidFormatException {
        TicHistoDataSet dataSet = new TicHistoDataSet(label, value, timestamp, checksum);
        dataSetList.add(index, dataSet);
        return dataSet;
    }


    @Override
    public TicDataSet getDataSet(String label) {
        TicDataSet dataSet = null;

        for(TicDataSet parsedDataSet : dataSetList){
            if(parsedDataSet.getLabel().equals(label)){
                dataSet = parsedDataSet;
                break;
            }
        }
        return dataSet;
    }

    @Override
    public byte[] getBytes() {

        byte[] bytes = new byte[0];
        byte[] startOfFrame = new byte[]{0x02}; // STX
        byte[] endOfFrame = new byte[]{0x03}; // ETX

        bytes = ByteArray.append(bytes,startOfFrame);
        for(TicDataSet dataSet : dataSetList){
            bytes = ByteArray.append(bytes,dataSet.getBytes());
        }
        bytes = ByteArray.append(bytes,endOfFrame);

        return bytes;
    }
}
