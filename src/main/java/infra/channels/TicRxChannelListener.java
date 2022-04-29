package infra.channels;

import arrays.ByteArray;

public interface TicRxChannelListener extends TicChannelListener {
    void onDataReceived();
}
