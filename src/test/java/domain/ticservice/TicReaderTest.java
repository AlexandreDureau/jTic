package domain.ticservice;

import domain.exceptions.TicInvalidConfigException;
import domain.ticframe.TicFrame;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TicReaderTest extends TicServiceTest{

    protected static final String RESSOURCES_DIR = TEST_RESOURCES_DIR + "/ticreader";

    private List<TicFrame> rxTicFrames = new ArrayList<TicFrame>();


    @Test
    public void test_ticreader_with_serialport_channel() throws TicInvalidConfigException, IOException, InterruptedException {

        TicHandler ticHandler = new TicHandler();
        TicReader ticReader = TicReader.getInstance(ticHandler, RESSOURCES_DIR + "/TicReaderConfig_TicHistoAndSerialPortChannel.json");

        ticReader.start();
        wait(10);
        ticReader.stop();
    }


    @Test
    public void test_ticreader_with_a_file_channel() throws TicInvalidConfigException, IOException, InterruptedException {

        TicHandler ticHandler = new TicHandler();
        TicReader ticReader = TicReader.getInstance(ticHandler, RESSOURCES_DIR + "/TicReaderConfig_TicHistoAndFileChannel.json");

        ticReader.start();
        wait(10);
        ticReader.stop();
    }


    @Override
    public void onReceivedFrame(TicFrame frame) {

    }

    @Override
    public void onInvalidDataReceived(byte[] data) {

    }

    @Override
    public void onStartOfTransmission() {

    }

    @Override
    public void onEndOfTransmission() {

    }
}
