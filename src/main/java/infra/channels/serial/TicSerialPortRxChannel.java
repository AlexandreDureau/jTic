package infra.channels.serial;

import arrays.ByteArray;
import com.fazecast.jSerialComm.SerialPort;
import domain.exceptions.TicInvalidConfigException;
import infra.channels.TicRxChannel;
import infra.channels.TicRxChannelListener;
import org.json.JSONException;
import org.json.JSONObject;
import types.TicMode;

import java.io.IOException;
import java.util.List;

import static constants.Constants.TIC_FRAME_START_KEY;
import static constants.Constants.TIC_FRAME_STOP_KEY;
import static infra.channels.serial.TicSerialPortConstants.*;

public class TicSerialPortRxChannel extends TicRxChannel {

    String portName = "";
    SerialPort serialPort;
    int baudRate;
    int dataBits;
    int stopBits;
    int parity;

    public TicSerialPortRxChannel(TicRxChannelListener listener) {
        super(listener);
    }

    public TicSerialPortRxChannel(JSONObject config) throws TicInvalidConfigException {
        super(null);
        this.setConfig(config);
    }

    public TicSerialPortRxChannel(TicRxChannelListener listener, JSONObject config) throws TicInvalidConfigException {
        super(listener);
        this.setConfig(config);
    }

    @Override
    public void start() {
        serialPort = SerialPort.getCommPort(portName);
        serialPort.setComPortParameters(baudRate,dataBits,stopBits,parity);
        serialPort.openPort();

        this.rxProcess = new Thread(new Runnable()
        {
            @Override public synchronized void run()
            {
                try
                {
                    isStarted = true;
                    while(isStarted)
                    {
                        int nbBytesToRead = serialPort.bytesAvailable();

                        if(nbBytesToRead > 0){
                            byte[] buffer = new byte[nbBytesToRead];
                            serialPort.readBytes(buffer, buffer.length);
                            accumulate(buffer);
                        }

                        Thread.sleep(10);
                    }
                }

                catch(Exception except)
                {
                    except.printStackTrace();
                }
            }
        });

        this.rxProcess.start();
    }

    @Override
    public void stop() {
        serialPort.closePort();
        this.startOfFrameDetected = false;
        this.accumulator.clear();
    }


    @Override
    public void setConfig(JSONObject config) throws TicInvalidConfigException {
        TicMode ticMode;

        try{
            ticMode = stringToTicMode(this.getStringParameter(config,"ticMode", true, "HISTO"));
        }
        catch(IllegalArgumentException e){
            throw new TicInvalidConfigException(e.getMessage());
        }

        String portName = this.getStringParameter(config,"portName",true,"");

        if((ticMode != this.ticMode) || (!portName.equals(this.portName))){
            this.setTicMode(ticMode);
            this.portName = portName;

            if(isStarted){
                this.stop();
                this.start();
            }
        }
    }


    protected  void setTicMode(TicMode ticMode) throws TicInvalidConfigException {
        this.ticMode = ticMode;
        switch(ticMode){
            case HISTO:
                this.baudRate = TIC_HISTO_BAUDRATE;
                this.dataBits = TIC_HISTO_DATABITS;
                this.stopBits = TIC_HISTO_STOPBITS;
                this.parity   = TIC_HISTO_PARITY;
                break;
            case STANDARD:
                this.baudRate = TIC_STANDARD_BAUDRATE;
                this.dataBits = TIC_STANDARD_DATABITS;
                this.stopBits = TIC_STANDARD_STOPBITS;
                this.parity   = TIC_STANDARD_PARITY;
                break;
            default         : throw new TicInvalidConfigException("ticMode " + ticMode + " is not handled");
        }
    }


    protected void accumulate(byte[] buffer){

        accumulator.append(buffer);

        List<ByteArray> ticDataSegments = accumulator.split(TIC_FRAME_START_KEY, TIC_FRAME_STOP_KEY, -1);

        if(!ticDataSegments.isEmpty()){

            ByteArray lastTicDataSegment = ticDataSegments.get(ticDataSegments.size() -1);
            if(lastTicDataSegment.endsWith(TIC_FRAME_STOP_KEY)){

                accumulator.clear();
                for(ByteArray ticDataSegment : ticDataSegments){
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
}
