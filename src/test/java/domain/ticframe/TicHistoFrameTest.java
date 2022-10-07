package domain.ticframe;

import domain.exceptions.*;
import domain.ticdataset.TicDataSet;
import domain.ticdataset.TicHistoDataSet;
import domain.ticdataset.TicStandardDataSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class TicHistoFrameTest extends TicFrameTest{

    private final byte STX = 2;
    private final byte ETX = 3;

    @Test
    public void getDataSetList_method_shall_return_the_list_of_all_dataset(){
        // Given:
        TicHistoFrame ticHistoFrame = getValidFrameWithSeveralDataSets();

        // When:
        List<TicHistoDataSet> dataSetList = ticHistoFrame.getDataSetList();

        // Then:
        Assertions.assertTrue(dataSetListContains(dataSetList, "BASE"));
        Assertions.assertTrue(dataSetListContains(dataSetList, "PAPP"));
        Assertions.assertTrue(dataSetListContains(dataSetList, "OPTARIF"));
    }

    @Test
    public void getDataSetList_method_shall_return_an_empty_list_if_there_is_no_dataset_in_the_frame(){
        // Given:
        TicHistoFrame ticHistoFrame = getValidEmptyFrame();

        // When:
        // Then:
        Assertions.assertTrue(ticHistoFrame.getDataSetList().isEmpty());
    }


    @Test
    public void clear_method_shall_remove_all_dataset_from_a_frame() {
        // Given:
        TicHistoFrame ticHistoFrame = getValidFrameWithSeveralDataSets();
        Assertions.assertFalse(ticHistoFrame.getDataSetList().isEmpty());

        // When:
        ticHistoFrame.clear();

        // Then:
        Assertions.assertTrue(ticHistoFrame.getDataSetList().isEmpty());
    }


    @Test
    public void getDataSet_method_shall_return_the_dataSet_with_given_label() {
        // Given:
        TicHistoFrame ticHistoFrame = getValidFrameWithSeveralDataSets();

        // When:
        TicDataSet dataSet = ticHistoFrame.getDataSet("BASE");

        // Then:
        Assertions.assertEquals("BASE", dataSet.getLabel());
        Assertions.assertEquals("000000095", dataSet.getValue());
    }


    public void getDataSet_method_shall_return_null_if_the_given_label_does_not_exist() {
        // Given:
        TicHistoFrame ticHistoFrame = getValidFrameWithSeveralDataSets();

        // When:
        // Then:
        Assertions.assertNull(ticHistoFrame.getDataSet("UNKNOWN_LABEL"));
    }


    @Test
    public void indexOf_method_shall_return_the_index_of_the_dataset_in_the_list() {
        // Given:
        TicHistoFrame ticHistoFrame = getValidFrameWithSeveralDataSets();

        // When:
        // Then:
        Assertions.assertEquals(1, ticHistoFrame.indexOf("PAPP"));
    }


    @Test
    public void indexOf_method_shall_return_a_negative_value_if_the_dataset_with_the_given_label_is_not_found() {
        // Given:
        TicHistoFrame ticHistoFrame = getValidFrameWithSeveralDataSets();

        // When:
        // Then:
        Assertions.assertEquals(-1,ticHistoFrame.indexOf("UNKNOWN_LABEL"));
    }


    @Test
    public void addDataSet_method_shall_add_a_dataSet_if_no_dataSet_with_given_label_exists_in_the_frame() throws TicDataSetAlreadyExistsException, TicDataSetUnexpectedTypeException {
        // Given:
        TicHistoFrame ticHistoFrame = getValidFrameWithSeveralDataSets();
        TicDataSet ptecDataSet = new TicHistoDataSet("PTEC");

        // When:
        ticHistoFrame.addDataSet(ptecDataSet);

        // Then:
        Assertions.assertEquals(ptecDataSet, ticHistoFrame.getDataSet("PTEC"));
    }


    @Test
    public void addDataSet_method_shall_throw_an_exception_if_the_dataset_already_exists() {
        // Given:
        TicHistoFrame ticHistoFrame = getValidFrameWithSeveralDataSets();

        // When:
        // Then:
        Assertions.assertThrows(TicDataSetAlreadyExistsException.class, () ->
                ticHistoFrame.addDataSet(new TicHistoDataSet("BASE")), "DataSet BASE already exists"
        );
    }


    @Test
    public void addDataSet_method_shall_throw_an_exception_if_the_dataset_has_not_a_consistent_type() {
        // Given:
        TicHistoFrame ticHistoFrame = getValidFrameWithSeveralDataSets();

        // When:
        // Then:
        Assertions.assertThrows(TicDataSetUnexpectedTypeException.class, () ->
                ticHistoFrame.addDataSet(new TicStandardDataSet()), "Invalid DataSet type. Expected <TicHistoDataSet> , Actual <TicStandardDataSet>"
        );
    }


    @Test
    public void addDataSet_method_shall_add_a_dataSet_at_the_given_index_if_in_the_range() throws TicDataSetAlreadyExistsException, TicDataSetUnexpectedTypeException {
        // Given:
        TicHistoFrame ticHistoFrame = getValidFrameWithSeveralDataSets();
        Assertions.assertTrue(ticHistoFrame.indexOf("PTEC") <0);
        TicDataSet ptecDataSet = new TicHistoDataSet("PTEC");

        // When:
        ticHistoFrame.addDataSet(1,ptecDataSet);

        // Then:
        Assertions.assertEquals(ptecDataSet, ticHistoFrame.getDataSetList().get(1));
    }


    @Test
    public void addDataSet_method_shall_add_a_dataSet_at_the_end_of_the_list_if_the_given_index_is_negative() throws TicDataSetAlreadyExistsException, TicDataSetUnexpectedTypeException {
        // Given:
        TicHistoFrame ticHistoFrame = getValidFrameWithSeveralDataSets();
        Assertions.assertTrue(ticHistoFrame.indexOf("PTEC") <0);
        TicDataSet ptecDataSet = new TicHistoDataSet("PTEC");

        // When:
        ticHistoFrame.addDataSet(-1,ptecDataSet);

        // Then:
        int length = ticHistoFrame.getDataSetList().size();
        Assertions.assertEquals(4,length);
        Assertions.assertEquals(ptecDataSet, ticHistoFrame.getDataSetList().get(length-1));
    }


    @Test
    public void addDataSet_method_shall_add_a_dataSet_at_the_end_of_the_list_if_the_given_index_is_out_of_range() throws TicDataSetAlreadyExistsException, TicDataSetUnexpectedTypeException {
        // Given:
        TicHistoFrame ticHistoFrame = getValidFrameWithSeveralDataSets();
        Assertions.assertTrue(ticHistoFrame.indexOf("PTEC") < 0);
        TicDataSet ptecDataSet = new TicHistoDataSet("PTEC");
        Assertions.assertEquals(3, ticHistoFrame.getDataSetList().size());

        // When:
        ticHistoFrame.addDataSet(3, ptecDataSet);

        // Then:
        int length = ticHistoFrame.getDataSetList().size();
        Assertions.assertEquals(4, length);
        Assertions.assertEquals(ptecDataSet, ticHistoFrame.getDataSetList().get(3));
    }

    @Test
    public void addDataSet_method_shall_move_a_dataSet_at_the_given_index_of_the_list_and_update_its_value_if_the_dataSet_already_exists() throws TicInvalidFormatException, TicDataSetAlreadyExistsException, TicDataSetUnexpectedTypeException {
        // Given:
        TicHistoFrame ticHistoFrame = getValidFrameWithSeveralDataSets();
        Assertions.assertEquals(1, ticHistoFrame.indexOf("PAPP"));
        Assertions.assertEquals(3, ticHistoFrame.getDataSetList().size());

        // When:
        ticHistoFrame.addDataSet(0, new TicHistoDataSet("PAPP", "NewValue"));

        // Then:
        Assertions.assertEquals(3, ticHistoFrame.getDataSetList().size());
        Assertions.assertEquals("PAPP", ticHistoFrame.getDataSetList().get(0).getLabel());
        Assertions.assertEquals("NewValue", ticHistoFrame.getDataSetList().get(0).getValue());
        Assertions.assertEquals("BASE", ticHistoFrame.getDataSetList().get(1).getLabel());
        Assertions.assertEquals("000000095", ticHistoFrame.getDataSetList().get(1).getValue());
        Assertions.assertEquals("OPTARIF", ticHistoFrame.getDataSetList().get(2).getLabel());
        Assertions.assertEquals("BASE", ticHistoFrame.getDataSetList().get(2).getValue());
    }

    @Test
    public void removeDataSet_method_shall_remove_the_dataset_with_the_given_label() {
        // Given:
        TicHistoFrame ticHistoFrame = getValidFrameWithSeveralDataSets();
        Assertions.assertEquals(1, ticHistoFrame.indexOf("PAPP"));
        Assertions.assertEquals(3, ticHistoFrame.getDataSetList().size());

        // When:
        ticHistoFrame.removeDataSet("PAPP");

        // Then:
        Assertions.assertEquals(2, ticHistoFrame.getDataSetList().size());
        Assertions.assertTrue(ticHistoFrame.indexOf("PAPP") < 0);
    }

    @Test
    public void removeDataSetAt_method_shall_remove_the_dataset_at_the_given_index() {
        // Given:
        TicHistoFrame ticHistoFrame = getValidFrameWithSeveralDataSets();
        Assertions.assertEquals(1, ticHistoFrame.indexOf("PAPP"));
        Assertions.assertEquals(3, ticHistoFrame.getDataSetList().size());

        // When:
        ticHistoFrame.removeDataSetAt(1);

        // Then:
        Assertions.assertEquals(2, ticHistoFrame.getDataSetList().size());
        Assertions.assertTrue(ticHistoFrame.indexOf("PAPP") < 0);
    }


    @Test
    public void removeDataSetAt_method_shall_remove_the_last_dataset_if_the_given_index_is_negative() {
        // Given:
        TicHistoFrame ticHistoFrame = getValidFrameWithSeveralDataSets();
        Assertions.assertEquals(2, ticHistoFrame.indexOf("OPTARIF"));
        Assertions.assertEquals(3, ticHistoFrame.getDataSetList().size());

        // When:
        ticHistoFrame.removeDataSetAt(-1);

        // Then:
        Assertions.assertEquals(2, ticHistoFrame.getDataSetList().size());
        Assertions.assertTrue(ticHistoFrame.indexOf("OPTARIF") < 0);
    }


    @Test
    public void removeDataSetAt_method_shall_remove_the_last_dataset_if_the_given_index_is_out_of_range() {
        // Given:
        TicHistoFrame ticHistoFrame = getValidFrameWithSeveralDataSets();
        Assertions.assertEquals(2, ticHistoFrame.indexOf("OPTARIF"));
        Assertions.assertEquals(3, ticHistoFrame.getDataSetList().size());

        // When:
        ticHistoFrame.removeDataSetAt(5);

        // Then:
        Assertions.assertEquals(2, ticHistoFrame.getDataSetList().size());
        Assertions.assertTrue(ticHistoFrame.indexOf("OPTARIF") < 0);
    }


    @Test
    public void moveDataSetAt_method_shall_move_the_dataset_with_given_label_at_the_given_index_if_dataset_exists_and_index_is_valid() throws TicDataSetNotFoundException {
        // Given:
        TicHistoFrame ticHistoFrame = getValidFrameWithSeveralDataSets();
        Assertions.assertEquals(2, ticHistoFrame.indexOf("OPTARIF"));
        Assertions.assertEquals(3, ticHistoFrame.getDataSetList().size());

        // When:
        ticHistoFrame.moveDataSet("OPTARIF",0);

        // Then:
        Assertions.assertEquals(3, ticHistoFrame.getDataSetList().size());
        Assertions.assertEquals("OPTARIF", ticHistoFrame.getDataSetList().get(0).getLabel());
        Assertions.assertEquals("BASE", ticHistoFrame.getDataSetList().get(1).getLabel());
        Assertions.assertEquals("PAPP", ticHistoFrame.getDataSetList().get(2).getLabel());
    }


    @Test
    public void moveDataSetAt_method_shall_move_the_dataset_with_given_label_at_the_end_of_the_list_if_dataset_exists_and_index_is_negative() throws TicDataSetNotFoundException {
        // Given:
        TicHistoFrame ticHistoFrame = getValidFrameWithSeveralDataSets();
        Assertions.assertEquals(1, ticHistoFrame.indexOf("PAPP"));
        Assertions.assertEquals(3, ticHistoFrame.getDataSetList().size());

        // When:
        ticHistoFrame.moveDataSet("PAPP",-1);

        // Then:
        Assertions.assertEquals(3, ticHistoFrame.getDataSetList().size());
        Assertions.assertEquals("BASE", ticHistoFrame.getDataSetList().get(0).getLabel());
        Assertions.assertEquals("OPTARIF", ticHistoFrame.getDataSetList().get(1).getLabel());
        Assertions.assertEquals("PAPP", ticHistoFrame.getDataSetList().get(2).getLabel());
    }


    @Test
    public void moveDataSetAt_method_shall_move_the_dataset_with_given_label_at_the_end_of_the_list_if_dataset_exists_and_index_is_out_of_range() throws TicDataSetNotFoundException {
        // Given:
        TicHistoFrame ticHistoFrame = getValidFrameWithSeveralDataSets();
        Assertions.assertEquals(1, ticHistoFrame.indexOf("PAPP"));
        Assertions.assertEquals(3, ticHistoFrame.getDataSetList().size());

        // When:
        ticHistoFrame.moveDataSet("PAPP",3);

        // Then:
        Assertions.assertEquals(3, ticHistoFrame.getDataSetList().size());
        Assertions.assertEquals("BASE", ticHistoFrame.getDataSetList().get(0).getLabel());
        Assertions.assertEquals("OPTARIF", ticHistoFrame.getDataSetList().get(1).getLabel());
        Assertions.assertEquals("PAPP", ticHistoFrame.getDataSetList().get(2).getLabel());
    }


    @Test
    public void moveDataSetAt_method_shall_throw_an_exception_if_no_dataset_with_given_label_exists() {
        // Given:
        TicHistoFrame ticHistoFrame = getValidFrameWithSeveralDataSets();

        // When:

        // When:
        // Then:
        Assertions.assertThrows(TicDataSetNotFoundException.class, () ->
                ticHistoFrame.moveDataSet("UNKNOWN_DATASET",1), "DataSet <UNKNOWN_DATASET> not found"
        );
    }

    @Test
    public void toString_method_shall_return_a_string_representing_an_empty_frame() {
        // Given:
        TicHistoFrame ticHistoFrame = getValidEmptyFrame();

        // When:
        String ticHistoFrameStr = ticHistoFrame.toString();

        // Then:
        Assertions.assertEquals("<STX><ETX>",ticHistoFrameStr);
    }

    @Test
    public void toString_method_shall_return_a_string_representing_a_frame_with_datasets() {
        // Given:
        TicHistoFrame ticHistoFrame = getValidFrameWithSeveralDataSets();

        // When:
        String ticHistoFrameStr = ticHistoFrame.toString();

        // Then:
        Assertions.assertEquals("<STX><BASE 000000095 Y><PAPP 00000 !><OPTARIF BASE 0><ETX>",ticHistoFrameStr);
    }

    @Test
    public void toText_method_shall_return_a_text_as_a_string_representing_a_frame_with_datasets() throws UnsupportedEncodingException {
        // Given:
        TicHistoFrame ticHistoFrame = getValidFrameWithSeveralDataSets();

        // When:
        String ticHistoFrameTxt = ticHistoFrame.toText();

        // Then:
        String expectedTicHistoFrameTxt =
                new String("<STX>")+
                "\n" + "BASE 000000095 Y" + "\r" +
                "\n" + "PAPP 00000 !" + "\r" +
                "\n" + "OPTARIF BASE 0" + "\r" + new String("<ETX>");

        Assertions.assertEquals(expectedTicHistoFrameTxt, ticHistoFrameTxt);
    }


    @Test
    public void getBytes_method_shall_return_an_array_of_2_bytes_STX_ETX_when_a_frame_is_empty() {
        // Given:
        TicHistoFrame ticHistoFrame = getValidEmptyFrame();

        // When:
        byte[] ticHistoFrameBytes = ticHistoFrame.getBytes();

        // Then:
        byte[] expectedBytes = {STX,ETX};
        Assertions.assertArrayEquals(expectedBytes,ticHistoFrameBytes);
    }

    @Test
    public void getBytes_method_shall_return_an_array_of_byte_when_a_frame_has_dataset() {
        // Given:
        TicHistoFrame ticHistoFrame = getValidFrameWithSeveralDataSets();

        // When:
        byte[] ticHistoFrameBytes = ticHistoFrame.getBytes();

        // Then:
        byte[] expectedBytes = {2,
                10,66,65,83,69,32,48,48,48,48,48,48,48,57,53,32,89,13,
                10,80,65,80,80,32,48,48,48,48,48,32,33,13,
                10,79,80,84,65,82,73,70,32,66,65,83,69,32,48,13,
                3};

        Assertions.assertArrayEquals(expectedBytes, ticHistoFrameBytes);
    }


    private TicHistoFrame getValidFrameWithSeveralDataSets(){

        TicHistoFrame ticHistoFrame = new TicHistoFrame();

        try {
            ticHistoFrame.addDataSet(new TicHistoDataSet("BASE", "000000095"));
            ticHistoFrame.addDataSet(new TicHistoDataSet("PAPP", "00000"));
            ticHistoFrame.addDataSet(new TicHistoDataSet("OPTARIF", "BASE"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return ticHistoFrame;
    }

    private TicHistoFrame getValidEmptyFrame()  {
        return new TicHistoFrame();
    }
}
