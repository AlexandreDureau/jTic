package infra.channels;

import arrays.ByteArray;
import domain.exceptions.TicInvalidConfigException;
import org.json.JSONException;
import org.json.JSONObject;
import queue.Queue;
import queue.QueueListener;
import types.TicMode;

public abstract class TicRxChannel implements TicChannel, QueueListener {

    public static final int DFLT_END_OF_DATA_TIMEOUT = 250;
    protected TicMode ticMode = TicMode.HISTO;
    protected TicRxChannelListener listener;
    protected boolean isStarted = false;
    protected Thread rxProcess;
    protected ByteArray accumulator;
    protected boolean startOfFrameDetected;
    protected Queue<ByteArray> ticDataQueue;
    protected int endOfDataTimeout;
    protected int endOfDataTimeoutDuration;

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
        endOfDataTimeout = 0;
        endOfDataTimeoutDuration = DFLT_END_OF_DATA_TIMEOUT;
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


    protected void handleEndOfData(int duration){
        if(endOfDataTimeout > 0){
            endOfDataTimeout -= duration;

            if((endOfDataTimeout <= 0) && (listener != null)){
                listener.onDataEnd(this);
            }
        }
    }


    protected void handleBeginningOfData(){
        if((endOfDataTimeout <= 0)&& (listener != null)){
            listener.onDataBeginning(this);
        }
        endOfDataTimeout = endOfDataTimeoutDuration;
    }


    public int getEndOfDataTimeoutDuration(){
        return endOfDataTimeoutDuration;
    }


    public void setEndOfDataTimeoutDuration(int timeoutDuration){
        if(timeoutDuration > 0){
            endOfDataTimeoutDuration = timeoutDuration;
        }
    }
}
