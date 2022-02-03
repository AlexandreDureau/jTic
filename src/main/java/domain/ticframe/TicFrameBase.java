package domain.ticframe;

import arrays.ByteArray;
import domain.exceptions.TicInvalidFormatException;
import domain.ticdataset.TicDataSet;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static constants.Constants.TIC_FRAME_START_KEY;
import static constants.Constants.TIC_FRAME_STOP_KEY;

public abstract class TicFrameBase implements TicFrame{

    public TicFrameBase(){

    }

    public TicFrameBase(ByteArray data) throws TicInvalidFormatException {

        if(!data.startsWith(TIC_FRAME_START_KEY))
        {
            throw new TicInvalidFormatException("TicFrame should start with STX");
        }

        if(!data.endsWith(TIC_FRAME_STOP_KEY))
        {
            throw new TicInvalidFormatException("TicFrame should end with ETX");
        }
    }

    public int indexOf(String label){
        int index = -1;

        for(int i = 0; i<this.getDataSetList().size(); i++){
            if(label.equals(this.getDataSetList().get(i).getLabel())){
                index = i;
                break;
            }
        }

        return index;
    }


    @Override
    public String toString(){
        StringBuilder tic_frame_str = new StringBuilder("<STX>");

        for(TicDataSet dataset : this.getDataSetList()){
            String dataset_str = dataset.toString();
            tic_frame_str.append(dataset_str);
        }

        tic_frame_str.append("<ETX>");

        return tic_frame_str.toString();
    }


    @Override
    public String toText(){

        StringBuilder text = new StringBuilder();
        try {

            for(TicDataSet dataset : this.getDataSetList()){
                text.append(new String(dataset.getBytes(), "UTF-8"));
            }
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return text.toString();
    }

    public abstract  byte[] getBytes();

    protected abstract <T extends TicDataSet> void addNewDataset(int index, T dataSet);
}
