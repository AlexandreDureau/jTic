package infra.channels.file;

import arrays.ByteArray;
import com.fazecast.jSerialComm.SerialPort;
import domain.exceptions.TicInvalidConfigException;
import infra.channels.TicRxChannel;
import infra.channels.TicRxChannelListener;
import org.json.JSONException;
import org.json.JSONObject;
import types.TicMode;

import java.io.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static constants.Constants.TIC_FRAME_START_KEY;
import static constants.Constants.TIC_FRAME_STOP_KEY;

public class TicFileRxChannel extends TicRxChannel {

    public static final byte[] TIC_FRAME_START                    = new byte[]{'S','T','X'};
    public static final byte[] TIC_FRAME_END                      = new byte[]{'E','T','X'};
    public static final int MIN_TIC_HISTO_FRAME_READING_SPEED     = 1200 / 20;
    public static final int MIN_TIC_STANDARD_FRAME_READING_SPEED  = 9600 / 20;
    public static final int MAX_TIC_HISTO_FRAME_READING_SPEED     = 1200 / 10;
    public static final int MAX_TIC_STANDARD_FRAME_READING_SPEED  = 9600 / 10;
    public static final int DFLT_TIC_HISTO_FRAME_READING_SPEED    = 1200 / 10;
    public static final int DFLT_TIC_STANDARD_FRAME_READING_SPEED = 9600 / 10;
    public  static final int DFLT_LOOP = 1;

    private String filePath;
    private int baudrate = -1;
    private int interFrameDelay = 100; // miliseconds
    private int loop = DFLT_LOOP;
    private int loopIterator;
    private byte[] accumulatorDummy = new byte[0];
    private BufferedReader reader;
    public TicFileRxChannel(TicRxChannelListener listener) {
        super(listener);
    }


    public TicFileRxChannel(JSONObject config) throws TicInvalidConfigException {
        super(null);
        this.setConfig(config);
    }


    public TicFileRxChannel(TicRxChannelListener listener, JSONObject config) throws TicInvalidConfigException {
        super(listener);
        this.setConfig(config);
    }


    @Override
    public void start() throws IOException {
        FileInputStream  fileReader = new FileInputStream (filePath);
        reader = new BufferedReader(new InputStreamReader(fileReader));
        int currentBaudrate = getBaudrate();
        int sleepTime = 20;
        int nbCharsToReadPerCycle = currentBaudrate / (1000/sleepTime);
        loopIterator = 0;
        char[] readBuffer = new char[512];

        this.rxProcess = new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                try {
                    accumulator.clear();
                    isStarted = true;
                    while (isStarted) {

                        int nbReadChars = reader.read(readBuffer,0,nbCharsToReadPerCycle);

                        if (nbReadChars > 0) {
                            handleBeginningOfData();
                            accumulate(Arrays.copyOf(readBuffer, nbReadChars));
                        }
                        else{
                            handleEndOfData(sleepTime);

                            loopIterator++;
                            if((loop < 0) || (loopIterator < loop)){
                                fileReader.getChannel().position(0);
                            }
                        }
                        Thread.sleep(sleepTime);

                    }
                } catch (Exception except) {
                    except.printStackTrace();
                }
            }
        });

        this.rxProcess.start();
    }


    @Override
    public void stop() throws IOException {
        isStarted = false;
        reader.close();
    }


    @Override
    public void setConfig(JSONObject config) throws TicInvalidConfigException {

        TicMode ticMode     = stringToTicMode(getStringParameter(config, "ticMode", true, ""));
        String filePath     = new File("").getAbsolutePath() + File.separator +  getStringParameter(config, "path", true, "");
        int readingSpeed    = getIntParameter(config, "readingSpeed", false, getMinReadingSpeed(), getMaxReadingSpeed() , getDefaultReadingSpeed());
        int interFrameDelay = getIntParameter(config, "interFrameDelay", false, 0, Integer.MAX_VALUE, 1000);
        loop                = getIntParameter(config, "repeat", false, Integer.MIN_VALUE, Integer.MAX_VALUE, 1);

        if((!ticMode.equals(this.ticMode)) || (!filePath.equals(this.filePath))){
            this.ticMode = ticMode;
            this.filePath = filePath;
            if(isStarted){
                try {
                    this.stop();
                    this.start();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }


    protected int getMinReadingSpeed(){
        return (ticMode == TicMode.HISTO) ? MIN_TIC_HISTO_FRAME_READING_SPEED : MIN_TIC_STANDARD_FRAME_READING_SPEED;
    }


    protected int getMaxReadingSpeed(){
        return (ticMode == TicMode.HISTO) ? MAX_TIC_HISTO_FRAME_READING_SPEED : MAX_TIC_STANDARD_FRAME_READING_SPEED;
    }


    protected int getDefaultReadingSpeed(){
        return (ticMode == TicMode.HISTO) ? DFLT_TIC_HISTO_FRAME_READING_SPEED : DFLT_TIC_STANDARD_FRAME_READING_SPEED;
    }


    protected int getIntParameter(JSONObject config, String key, boolean mandatory, int min, int max, int defaultValue) throws TicInvalidConfigException{
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


    protected String getStringParameter(JSONObject config, String key, boolean mandatory, String defaultValue) throws TicInvalidConfigException{
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


    protected void accumulate(char[] buffer){

        byte[] bytes_buffer = ByteArray.toBytes(buffer);
        accumulator.append(bytes_buffer);
        List<ByteArray> ticDataSegmentList = accumulator.split(TIC_FRAME_START_KEY, TIC_FRAME_STOP_KEY, -1);

        if(!ticDataSegmentList.isEmpty()){
            handleTicFrames(ticDataSegmentList);
            updateAccumulator(ticDataSegmentList);
        }
    }


    private void handleTicFrames(List<ByteArray> ticDataSegmentList) {
        for(ByteArray ticDataSegment : ticDataSegmentList){
            if(ticDataSegment.startsWith(TIC_FRAME_START_KEY) && ticDataSegment.endsWith(TIC_FRAME_STOP_KEY)){
                refactoTicDataSegment(ticDataSegment);
                this.ticDataQueue.push(ticDataSegment);
            }
        }
    }


    private void updateAccumulator(List<ByteArray> ticDataSegmentList){
        ByteArray lastTicDataSegment = ticDataSegmentList.get(ticDataSegmentList.size() -1);

        if(lastTicDataSegment.startsWith(TIC_FRAME_START_KEY) && lastTicDataSegment.endsWith(TIC_FRAME_STOP_KEY)){
            accumulator.clear();
        }
        else{
            accumulator = lastTicDataSegment;
        }
    }


    private void refactoTicDataSegment(ByteArray ticDataSegment){
        ticDataSegment.removeFirst(TIC_FRAME_START, 1);
        ticDataSegment.removeLast(TIC_FRAME_END, 1);
        ticDataSegment.insert(0, TIC_FRAME_START_KEY);
        ticDataSegment.append(TIC_FRAME_STOP_KEY);
    }


    public int getBaudrate(){
        int computedBaudrate;
        if(baudrate == -1){
            switch (ticMode){
                case HISTO   : computedBaudrate = 1200; break;
                case STANDARD:
                default      : computedBaudrate = 9600;
            }
        }
        else{
            computedBaudrate = baudrate;
        }

        return computedBaudrate;
    }
}
