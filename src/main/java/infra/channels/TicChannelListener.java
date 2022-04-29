package infra.channels;

public interface TicChannelListener {

    /**
     * Called when data are detected on the channel
     * @param channel
     */
    void onDataBeginning(TicChannel channel);

    /**
     * Called when data are no more detected on the channel
     * @param channel
     */
    void onDataEnd(TicChannel channel);

    /**
     * Called when a frame beginning pattern <STX> is read on the channel
     * @param channel
     */
    void onFrameBeginning(TicChannel channel);

    /**
     * Called when a frame end pattern <ETX> is read on the channel
     * @param channel
     */
    void onFrameEnd(TicChannel channel);

    /**
     * Called when the channel is started
     * @param channel
     */
    void onChannelStarted(TicChannel channel);

    /**
     * Called when the channel is stopped
     * @param channel
     */
    void onChannelStopped(TicChannel channel);
}
