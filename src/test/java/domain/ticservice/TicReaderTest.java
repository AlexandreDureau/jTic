package domain.ticservice;

import arrays.ByteArray;
import domain.exceptions.TicInvalidConfigException;
import domain.exceptions.TicInvalidFormatException;
import domain.ticdataset.TicHistoDataSet;
import domain.ticframe.TicFrame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TicReaderTest extends TicServiceTest implements TicListener{

    private List<TicFrame> ticFrameList = new ArrayList<>();
    private List<ByteArray> invalidData = new ArrayList<>();
    private boolean endOfTransmission;


    @BeforeEach
    void beforeEach(){
        endOfTransmission = false;
    }

    @Test
    void ticReader_should_load_historic_tic_config_from_a_config_file_and_read_historic_tic_from_file_in_a_explicit_single_loop() throws TicInvalidConfigException, IOException, InterruptedException, TicInvalidFormatException {
       // Given
        TicReader ticReader = TicReader.getInstance(this, this.getResourcesDir() + "/file/config/TicHistoFileChannelAndExplicitSingleLoop.json");

        // When
        ticReader.start();
        waitForEndOfTransmission();

        // Then
        assertThat(ticFrameList.size()).isEqualTo(5);
        assertThat(ticFrameList.get(0).getDataSetList()).usingRecursiveComparison().isEqualTo(List.of(
                new TicHistoDataSet("ADCO","031664001115"),
                new TicHistoDataSet("OPTARIF","HC.."),
                new TicHistoDataSet("ISOUSC","15"),
                new TicHistoDataSet("HCHC","000000000"),
                new TicHistoDataSet("HCHP","000000000"),
                new TicHistoDataSet("PTEC","HP.."),
                new TicHistoDataSet("IINST","000"),
                new TicHistoDataSet("IMAX","060"),
                new TicHistoDataSet("PAPP","00000"),
                new TicHistoDataSet("HHPHC","A"),
                new TicHistoDataSet("MOTDETAT","000000")));
        assertThat(ticFrameList.get(1).getDataSetList()).usingRecursiveComparison().isEqualTo(List.of(
                new TicHistoDataSet("ADCO","031664001115"),
                new TicHistoDataSet("OPTARIF","HC.."),
                new TicHistoDataSet("ISOUSC","15"),
                new TicHistoDataSet("HCHC","000000000"),
                new TicHistoDataSet("HCHP","000000000"),
                new TicHistoDataSet("PTEC","HP.."),
                new TicHistoDataSet("IINST","001"),
                new TicHistoDataSet("IMAX","060"),
                new TicHistoDataSet("PAPP","00000"),
                new TicHistoDataSet("HHPHC","A"),
                new TicHistoDataSet("MOTDETAT","000000")));
        assertThat(ticFrameList.get(2).getDataSetList()).usingRecursiveComparison().isEqualTo(List.of(
                new TicHistoDataSet("ADCO","031664001115"),
                new TicHistoDataSet("OPTARIF","HC.."),
                new TicHistoDataSet("ISOUSC","15"),
                new TicHistoDataSet("HCHC","000000000"),
                new TicHistoDataSet("HCHP","000000000"),
                new TicHistoDataSet("PTEC","HP.."),
                new TicHistoDataSet("IINST","002"),
                new TicHistoDataSet("IMAX","060"),
                new TicHistoDataSet("PAPP","00000"),
                new TicHistoDataSet("HHPHC","A"),
                new TicHistoDataSet("MOTDETAT","000000")));
        assertThat(ticFrameList.get(3).getDataSetList()).usingRecursiveComparison().isEqualTo(List.of(
                new TicHistoDataSet("ADCO","031664001115"),
                new TicHistoDataSet("OPTARIF","HC.."),
                new TicHistoDataSet("ISOUSC","15"),
                new TicHistoDataSet("HCHC","000000000"),
                new TicHistoDataSet("HCHP","000000000"),
                new TicHistoDataSet("PTEC","HP.."),
                new TicHistoDataSet("IINST","003"),
                new TicHistoDataSet("IMAX","060"),
                new TicHistoDataSet("PAPP","00000"),
                new TicHistoDataSet("HHPHC","A"),
                new TicHistoDataSet("MOTDETAT","000000")));
        assertThat(ticFrameList.get(4).getDataSetList()).usingRecursiveComparison().isEqualTo(List.of(
                new TicHistoDataSet("ADCO","031664001115"),
                new TicHistoDataSet("OPTARIF","HC.."),
                new TicHistoDataSet("ISOUSC","15"),
                new TicHistoDataSet("HCHC","000000000"),
                new TicHistoDataSet("HCHP","000000000"),
                new TicHistoDataSet("PTEC","HP.."),
                new TicHistoDataSet("IINST","004"),
                new TicHistoDataSet("IMAX","060"),
                new TicHistoDataSet("PAPP","00000"),
                new TicHistoDataSet("HHPHC","A"),
                new TicHistoDataSet("MOTDETAT","000000")));
    }

    @Override
    public void onReceivedFrame(TicReader ticReader, TicFrame frame) {

        ticFrameList.add(frame);
    }

    @Override
    public void onInvalidDataReceived(TicReader ticReader, byte[] data) {

        invalidData.add(new ByteArray(data));
    }

    @Override
    public void onDataBeginning(TicReader ticReader) {

    }

    @Override
    public void onDataEnd(TicReader ticReader) {
        System.out.println("onDataEnd : " + ticReader);
        endOfTransmission = true;
    }

    @Override
    public void onFrameBeginning(TicReader ticReader) {

    }

    @Override
    public void onFrameEnd(TicReader ticReader) {

    }

    @Override
    public void onChannelStarted(TicReader ticReader) {

    }

    @Override
    public void onChannelStopped(TicReader ticReader) {

    }


    @Override
    public String getResourcesDir() {
        return super.getResourcesDir() + "/ticreader";
    }


    protected void waitForEndOfTransmission() throws InterruptedException {
        while(!endOfTransmission){
            Thread.sleep(20);
        }
    }
}
