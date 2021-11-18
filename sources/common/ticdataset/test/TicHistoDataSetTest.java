package common.ticdataset.test;

import common.exceptions.TicChecksumException;
import common.exceptions.TicInvalidFormatException;
import common.ticdataset.src.TicDataSet;
import common.ticdataset.src.TicHistoDataSet;
import common.ticdataset.src.TicStandardDataSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class TicHistoDataSetTest {

    protected TicHistoDataSet ticDataSet;

    @ParameterizedTest
    @MethodSource("getValidDataWithConsistentChecksum")
    public void set_method_shall_compute_checksum_when_data_are_valid(String label, String value, LocalDateTime timestamp, Byte expectedChecksum) throws TicChecksumException, TicInvalidFormatException {

        // Given
        ticDataSet = getDataSet();

        // When
        ticDataSet.set(label,value,timestamp,TicDataSet.COMPUTE_CHECKSUM);

        // Then
        Assertions.assertEquals(expectedChecksum, ticDataSet.getChecksum());
    }


    @Test
    public void set_method_shall_throw_a_TicInvalidFormatException_when_the_label_is_empty() {

        // Given
        ticDataSet = getDataSet();

        // When
        // Then
        Assertions.assertThrows(TicInvalidFormatException.class, () -> ticDataSet.set("", "ValidValue", LocalDateTime.now(), TicDataSet.COMPUTE_CHECKSUM));
    }


    @Test
    public void set_method_shall_throw_a_TicInvalidFormatException_when_the_label_is_null() {

        // Given
        ticDataSet = getDataSet();

        // When
        // Then
        Assertions.assertThrows(TicInvalidFormatException.class, () -> ticDataSet.set(null, "ValidValue", LocalDateTime.now(), TicDataSet.COMPUTE_CHECKSUM));
    }


    @ParameterizedTest
    @MethodSource("getDataWithIllegalCharacters")
    public void set_method_shall_throw_a_TicInvalidFormatException_when_the_label_contains_illegal_characters(String label) {

        // Given
        ticDataSet = getDataSet();

        // When
        // Then
        Assertions.assertThrows(TicInvalidFormatException.class, () -> ticDataSet.set(label, "ValidValue", LocalDateTime.now(), TicDataSet.COMPUTE_CHECKSUM));
    }


    @Test
    public void set_method_shall_throw_a_TicInvalidFormatException_when_the_value_is_empty() {

        // Given
        ticDataSet = getDataSet();

        // When
        // Then
        Assertions.assertThrows(TicInvalidFormatException.class, () -> ticDataSet.set("ValidLabel", "", LocalDateTime.now(), TicDataSet.COMPUTE_CHECKSUM));
    }


    @Test
    public void set_method_shall_throw_a_TicInvalidFormatException_when_the_value_is_null() {

        // Given
        ticDataSet = getDataSet();

        // When
        // Then
        Assertions.assertThrows(TicInvalidFormatException.class, () -> ticDataSet.set("ValidLabel", null, LocalDateTime.now(), TicDataSet.COMPUTE_CHECKSUM));
    }


    @ParameterizedTest
    @MethodSource("getDataWithIllegalCharacters")
    public void set_method_shall_throw_a_TicInvalidFormatException_when_the_value_contains_illegal_characters(String value) {

        // Given
        ticDataSet = getDataSet();

        // When
        // Then
        Assertions.assertThrows(TicInvalidFormatException.class, () -> ticDataSet.set("ValidLabel", value, LocalDateTime.now(), TicDataSet.COMPUTE_CHECKSUM));
    }


    @ParameterizedTest
    @MethodSource("getValidDataWithInvalidChecksum")
    public void set_method_shall_throw_a_TicChecksumException_when_the_given_checksum_is_not_consistent(String label, String value, LocalDateTime timestamp, Byte checksum) {

        // Given
        ticDataSet = getDataSet();

        // When
        // Then
        Assertions.assertThrows(TicChecksumException.class, () -> ticDataSet.set(label, value, timestamp, checksum));
    }


    private static List<String> getDataWithIllegalCharacters() {
        List<String> dataWithIllegalCharacters = new ArrayList<>();

        for (char c = 0; c < 0x21; c++) {
            dataWithIllegalCharacters.add("My" + c + "Data");
        }

        char c = 0x7F;
        dataWithIllegalCharacters.add("My" + c + "Data");

        return dataWithIllegalCharacters;
    }


    private static Stream<Arguments> getValidDataWithConsistentChecksum(){
        return Stream.of(
                Arguments.of("ISOUSC", "60", null, (byte)0x3C),  // "<"
                Arguments.of("PAPP", "00000", null, (byte)0x21), // "!"
                Arguments.of("IINST", "000", null, (byte)0x57), // "W"
                Arguments.of("PTEC", "TH..", null, (byte)0x24), // "$"
                Arguments.of("BASE", "000000095", null,(byte)0x59), // "Y"
                Arguments.of("OPTARIF", "BASE", null, (byte)0x30) // "0"
        );
    }


    private static Stream<Arguments> getValidDataWithInvalidChecksum(){
        return Stream.of(
                Arguments.of("ISOUSC", "60", null, (byte)0x3E), // ">"
                Arguments.of("PAPP", "00000", null, (byte)0x3D), // "="
                Arguments.of("IINST", "000", null, (byte)0x77), // "w"
                Arguments.of("PTEC", "TH..", null, (byte)0x65), // "e"
                Arguments.of("BASE", "000000095", null, (byte)0x69), // "y"
                Arguments.of("OPTARIF", "BASE", null, (byte)0x31) // "1"
        );
    }


    private static TicHistoDataSet getDataSet(){
        return new TicHistoDataSet();
    }
}
