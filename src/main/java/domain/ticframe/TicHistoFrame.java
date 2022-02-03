package domain.ticframe;

import arrays.ByteArray;
import domain.exceptions.*;
import domain.ticdataset.TicDataSet;
import domain.ticdataset.TicHistoDataSet;

import java.util.ArrayList;
import java.util.List;

import static constants.Constants.*;

public class TicHistoFrame extends TicFrameBase{

    private final List<TicHistoDataSet> dataSetList = new ArrayList<>();

    public TicHistoFrame(){
        super();
    }

    public TicHistoFrame(ByteArray data, int consistencyCheck) throws TicException {
        super(data);
        List<ByteArray> parts = data.split(TIC_DATASET_START_KEY,TIC_DATASET_STOP_KEY, true, -1);

        for (ByteArray part : parts) {
            this.addDataSet(new TicHistoDataSet(part, consistencyCheck));
        }
    }

    @Override
    public TicHistoDataSet getDataSet(String label) {
        TicHistoDataSet dataSet = null;

        for(TicHistoDataSet parsedDataSet : dataSetList){
            if(parsedDataSet.getLabel().equals(label)){
                dataSet = parsedDataSet;
                break;
            }
        }

        return dataSet;
    }


    @Override
    public <T extends TicDataSet> void addDataSet(T dataSet) throws TicDataSetAlreadyExistsException, TicDataSetUnexpectedTypeException {

        if(dataSet instanceof TicHistoDataSet){

            TicDataSet existingDataSet = this.getDataSet(dataSet.getLabel());

            if (null == existingDataSet) {
                dataSetList.add((TicHistoDataSet) dataSet);
            }
            else {
                throw new TicDataSetAlreadyExistsException(dataSet.getLabel());
            }
        }
        else{
            throw new TicDataSetUnexpectedTypeException(TicHistoDataSet.class, dataSet.getClass());
        }
    }


    @Override
    public <T extends TicDataSet> void addDataSet(int index, T dataSet) throws TicDataSetAlreadyExistsException, TicDataSetUnexpectedTypeException {

        if(dataSet instanceof TicHistoDataSet){

            if (null == this.getDataSet(dataSet.getLabel())) {
                this.addNewDataset(index,dataSet);
            }
            else {
                int current_index = this.indexOf(dataSet.getLabel());

                if(current_index != index){
                    this.removeDataSet(dataSet.getLabel());
                    this.addNewDataset(index,dataSet);
                }
                else{
                    throw new TicDataSetAlreadyExistsException(dataSet.getLabel(), current_index);
                }
            }
        }
        else{
            throw new TicDataSetUnexpectedTypeException(TicHistoDataSet.class, dataSet.getClass());
        }
    }


    @Override
    public void moveDataSet(String label, int index) throws TicDataSetNotFoundException {

        int current_index = this.indexOf(label);

        if(current_index >=0){
            if(current_index != index){
                TicHistoDataSet dataSet = this.getDataSet(label);
                this.removeDataSetAt(current_index);

                if((index >=0) && (index<dataSetList.size())){
                    dataSetList.add(index, dataSet);
                }
                else{
                    dataSetList.add(dataSet);
                }
            }
        }
        else {
            throw new TicDataSetNotFoundException(label);
        }

    }


    @Override
    public void clear() {
        dataSetList.clear();
    }


    @Override
    public void removeDataSet(String label) {
        for(int i=0; i< dataSetList.size(); i++ ){
            TicHistoDataSet dataSet = dataSetList.get(i);
            if(dataSet.getLabel().equals(label)){
                dataSetList.remove(i);
                break;
            }
        }
    }


    @Override
    public void removeDataSetAt(int index) {

        if(index >= 0 && index < dataSetList.size()){
            dataSetList.remove(index);
        }
        else{
            dataSetList.remove(dataSetList.size()-1);
        }
    }


    @Override
    public List<TicHistoDataSet> getDataSetList() {
        return this.dataSetList;
    }

    @Override
    protected <T extends TicDataSet> void addNewDataset(int index, T dataSet) {
        if((index >=0) && (index < dataSetList.size())){
            dataSetList.add(index, (TicHistoDataSet) dataSet);
        }
        else
        {
            dataSetList.add((TicHistoDataSet) dataSet);
        }
    }

    @Override
    public byte[] getBytes() {

        byte[] bytesArray = new byte[0];

        bytesArray= ByteArray.s_append(bytesArray, TIC_FRAME_START_KEY);

        for(TicHistoDataSet dataSet : dataSetList){
            byte[] dataset_bytes = dataSet.getBytes();
            bytesArray= ByteArray.s_append(bytesArray, dataset_bytes);
        }

        bytesArray= ByteArray.s_append(bytesArray,TIC_FRAME_STOP_KEY);

        return bytesArray;
    }
}
