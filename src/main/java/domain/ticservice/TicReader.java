package domain.ticservice;

import arrays.ByteArray;
import domain.exceptions.TicException;
import domain.exceptions.TicInvalidConfigException;
import domain.ticframe.TicHistoFrame;
import infra.channels.TicChannel;
import infra.channels.TicRxChannelFactory;
import infra.channels.TicRxChannel;
import infra.channels.TicRxChannelListener;
import org.json.JSONException;
import org.json.JSONObject;
import types.TicMode;

import java.io.File;
import java.io.IOException;

import static constants.Constants.CHECK_ALL;
import static constants.Constants.NO_CONSISTENCY_CHECK;

public class TicReader extends AbstractTicService implements TicService, TicRxChannelListener {

    protected TicRxChannel channel;
    protected TicListener listener;
    protected int consistencyChecks = NO_CONSISTENCY_CHECK;

    private TicReader(TicListener listener, Object config) throws TicInvalidConfigException {
        this.listener = listener;
        this.set(config);
    }

    public static TicReader getInstance(TicListener listener, Object config) throws TicInvalidConfigException {
        return new TicReader(listener, config);
    }


    @Override
    public void start() throws IOException {
        channel.start();
    }

    @Override
    public void stop() throws IOException {
        channel.stop();
    }


    @Override
    public void onDataReceived() {

        while (true){
            ByteArray data = this.channel.readData();

            if(data != null){
                switch (channel.getTicMode()){
                    case HISTO     :
                        try {
                            TicHistoFrame ticFrame = new TicHistoFrame(data, consistencyChecks);

                            if(null != this.listener){
                                this.listener.onReceivedFrame(this, ticFrame);
                            }
                        } catch (TicException e) {
                            e.printStackTrace();
                        }
                        break;
                    case STANDARD  : break;
                    default        : break;
                }
            }
            else {
                break;
            }
        }
    }

    @Override
    protected void setTicChannel(JSONObject config) throws TicInvalidConfigException {
        boolean restart = false;

        if(null != channel){
            if(channel.isStarted()){
                restart = true;
                try {
                    channel.stop();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }

        channel = TicRxChannelFactory.buildChannel(config);
        channel.setListener(this);

        if(restart){
            try {
                channel.start();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void onDataBeginning(TicChannel channel) {
        if(null != listener){
            listener.onDataBeginning(this);
        }
    }

    @Override
    public void onDataEnd(TicChannel channel) {
        if(null != listener){
            listener.onDataEnd(this);
        }
    }

    @Override
    public void onFrameBeginning(TicChannel channel) {
        if(null != listener){
            listener.onFrameBeginning(this);
        }
    }

    @Override
    public void onFrameEnd(TicChannel channel) {
        if(null != listener){
            listener.onFrameEnd(this);
        }
    }

    @Override
    public void onChannelStarted(TicChannel channel) {
        if(null != listener){
            listener.onChannelStarted(this);
        }
    }

    @Override
    public void onChannelStopped(TicChannel channel) {
        if(null != listener){
            listener.onChannelStopped(this);
        }
    }
}
