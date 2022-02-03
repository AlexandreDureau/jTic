package domain.ticservice;

import arrays.ByteArray;
import domain.ticframe.TicFrame;
import infra.channels.TicRxChannelListener;

class TicHandler implements TicListener {

    @Override
    public void onReceivedFrame(TicFrame frame) {
        System.out.println(frame.toText());
    }


    @Override
    public void onInvalidDataReceived(byte[] data) {
        System.out.println("onInvalidDataReceived: " + "\n" + new ByteArray(data).toString());
    }


    @Override
    public void onStartOfTransmission() {
        System.out.println("onStartOfTransmission");
    }


    @Override
    public void onEndOfTransmission() {
        System.out.println("onEndOfTransmission");
    }
}