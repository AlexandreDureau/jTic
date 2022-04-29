package domain.ticservice;

import domain.ticframe.TicFrame;
import infra.channels.TicChannel;

public interface TicListener{
    void onReceivedFrame(TicReader ticReader, TicFrame frame);
    void onInvalidDataReceived(TicReader ticReader, byte[] data);

    /**
     * Called when data are detected on the TicReader
     * @param ticReader
     */
    void onDataBeginning(TicReader ticReader);

    /**
     * Called when no more data are detected on the TicReader
     * @param ticReader
     */
    void onDataEnd(TicReader ticReader);

    /**
     * Called when a Start Of Frame pattern <STX> has been detected
     * @param ticReader
     */
    void onFrameBeginning(TicReader ticReader);

    /**
     * Called when a End Of Frame pattern <ETX> has been detected
     * @param ticReader
     */
    void onFrameEnd(TicReader ticReader);


    /**
     * Called when the channel starts
     * @param ticReader
     */
    void onChannelStarted(TicReader ticReader);


    /**
     * Called when the channel stops
     * @param ticReader
     */
    void onChannelStopped(TicReader ticReader);
}
