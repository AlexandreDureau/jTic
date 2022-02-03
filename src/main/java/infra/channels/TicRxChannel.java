package infra.channels;

import arrays.ByteArray;
import domain.exceptions.TicInvalidConfigException;
import org.json.JSONException;
import org.json.JSONObject;
import queue.Queue;
import queue.QueueListener;
import types.TicMode;

public abstract class TicRxChannel implements TicChannel, QueueListener {

    protected TicMode ticMode = TicMode.HISTO;
    protected TicRxChannelListener listener;
    protected boolean isStarted = false;
    protected Thread rxProcess;
    protected ByteArray accumulator;
    protected boolean startOfFrameDetected;
    protected Queue<ByteArray> ticDataQueue;

    public TicRxChannel(TicRxChannelListener listener) {
        this.init(listener);
    }

    public void setListener(TicRxChannelListener listener) {
        this.listener = listener;
    }

    protected void init(TicRxChannelListener listener) {
        this.setListener(listener);
        isStarted = false;
        accumulator = new ByteArray();
        startOfFrameDetected = false;
        ticDataQueue = new Queue<ByteArray>(this);
        ticDataQueue.start();
    }


    @Override
    public void onQueueUpdated() {
        if (null != this.listener) {
            this.listener.onDataReceived();
        }
    }

    public ByteArray readData(){
        return ticDataQueue.pop();
    }

    public TicMode getTicMode() {
        return ticMode;
    }

    @Override
    public boolean isStarted() {
        return isStarted;
    }


    int getIntParameter(JSONObject config, String key, boolean mandatory, int min, int max, int defaultValue) throws TicInvalidConfigException{
        int param;
        try{
            param = config.getInt(key);
            if((param < min) || (param > max)){
                throw new TicInvalidConfigException(key  + " value should be in interval [" + min + "," + max + "]");
            }
        }catch(JSONException e){
            if(mandatory){
                throw new TicInvalidConfigException(e.getMessage());
            }
            else{
                param = defaultValue;
            }
        }

        return param;
    }


    protected String getStringParameter(JSONObject config, String key, boolean mandatory, String defaultValue) throws TicInvalidConfigException {
        String param;
        try{
            param = config.getString(key);
        }catch (JSONException e){
            if(mandatory){
                throw new TicInvalidConfigException(e.getMessage());
            }
            else{
                param = defaultValue;
            }
        }

        return param;
    }


    protected static TicMode stringToTicMode(String ticMode) throws IllegalArgumentException{
        switch(ticMode.toLowerCase()){
            case "histo" :
            case "historic" : return TicMode.HISTO;
            case "standard" : return TicMode.STANDARD;
            default         : throw new IllegalArgumentException("ticMode " + ticMode + " is not handled");
        }
    }
}
