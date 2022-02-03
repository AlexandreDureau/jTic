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

    String filePath;
    int readingSpeed = 1000; // nb chars per second
    int interFrameDelay = 100; // miliseconds
    int loop;


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

        System.out.println("filePath : " + filePath);
        FileReader fileReader = new FileReader(filePath);
        BufferedReader reader = new BufferedReader(fileReader);
        int nbChars = 20;
        char[] readBuffer = new char[512];
        this.rxProcess = new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                try {
                    isStarted = true;
                    while (isStarted) {

                        int nbReadChars = reader.read(readBuffer,0,nbChars);

                        if (nbReadChars > 0) {
                            accumulate(Arrays.copyOf(readBuffer, nbReadChars));
                        }
                        Thread.sleep(20);
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

    }


    @Override
    public void setConfig(JSONObject config) throws TicInvalidConfigException {

        TicMode ticMode     = stringToTicMode(getStringParameter(config, "ticMode", true, ""));
        String filePath     = new File("").getAbsolutePath() + File.separator +  getStringParameter(config, "path", true, "");
        int readingSpeed    = getIntParameter(config, "readingSpeed", false, getMinReadingSpeed(), getMaxReadingSpeed() , getDefaultReadingSpeed());
        int interFrameDelay = getIntParameter(config, "interFrameDelay", false, 0, Integer.MAX_VALUE, 1000);

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

        accumulator.append(ByteArray.toBytes(buffer));

        List<ByteArray> ticDataSegments = accumulator.split(TIC_FRAME_START, TIC_FRAME_END, true, -1);
        //System.out.println("accumulator = <" + accumulator.toString(16) + ">");

        //System.out.println("ticDataSegments length = " + ticDataSegments.size());
        if(!ticDataSegments.isEmpty()){
            ByteArray lastTicDataSegment = ticDataSegments.get(ticDataSegments.size() -1);
            if(lastTicDataSegment.endsWith(TIC_FRAME_END)){

                accumulator.clear();
                for(ByteArray ticDataSegment : ticDataSegments){
                    refactoTicDataSegment(ticDataSegment);
                    this.ticDataQueue.push(ticDataSegment);
                }
            }
            else{
                accumulator = lastTicDataSegment;
                for(ByteArray ticDataSegment : ticDataSegments){
                    if(ticDataSegment != lastTicDataSegment){
                        this.ticDataQueue.push(ticDataSegment);
                    }
                }
            }
        }
    }

    private void refactoTicDataSegment(ByteArray ticDataSegment){
        ticDataSegment.removeFirst(TIC_FRAME_START, 1);
        ticDataSegment.removeLast(TIC_FRAME_END, 1);
        ticDataSegment.insert(0, TIC_FRAME_START_KEY);
        ticDataSegment.append(TIC_FRAME_STOP_KEY);
    }
}
