package domain.ticframe;

import domain.ticdataset.TicDataSet;

import java.util.List;

public abstract class TicFrameTest {

    protected boolean dataSetListContains(List<? extends TicDataSet> dataSetList, String label){

        boolean contains = false;

        for(TicDataSet dataSet : dataSetList){

            if(dataSet.getLabel().equals(label)){
                contains = true;
                break;
            }
        }

        return contains;
    }
}
