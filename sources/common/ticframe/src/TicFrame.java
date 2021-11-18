package common.ticframe.src;

import common.ticdataset.src.TicDataSet;

import java.util.ArrayList;
import java.util.List;

public abstract class TicFrame {

    protected List<TicDataSet> dataSetList = new ArrayList<TicDataSet>();

    public void clear(){
        dataSetList.clear();
    }

    public abstract void addDataSet(TicDataSet dataSet);
    public abstract void addDataSet(int index, TicDataSet dataSet);

    public TicDataSet getDataSet(String label){
        TicDataSet dataset = null;
        for(int i=0; i<dataSetList.size(); i++){
            var parsed_dataSet = dataSetList.get(i);
            if(parsed_dataSet.getLabel().equals(label)){
                dataset = parsed_dataSet;
                break;
            }
        }

        return dataset;
    }
}
