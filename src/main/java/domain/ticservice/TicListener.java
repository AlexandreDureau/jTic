package domain.ticservice;

import domain.ticframe.TicFrame;

public interface TicListener{
    void onReceivedFrame(TicFrame frame);
    void onInvalidDataReceived(byte[] data);
    void onStartOfTransmission();
    void onEndOfTransmission();
}
