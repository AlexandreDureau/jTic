package common.ticdataset.src;

import common.exceptions.TicChecksumException;
import common.exceptions.TicInvalidFormatException;

public class TicStandardDataSet extends TicDataSet{

    @Override
    protected Byte computeChecksum() {

        if(null != label && null != value){
            int sum=0;

            // Ajout des octets du label
            byte[] label_bytes = label.getBytes();
            for (byte character : label_bytes) {
                sum += character;
            }

            // Ajout des octets du timestamp
            if(null != timestamp){
                sum += SEP_CHAR;
                byte[] timestamp_bytes = timestamp.get().getBytes();
                for (byte character : timestamp_bytes) {
                    sum += character;
                }
            }

            // Ajout des octets de la valeur
            sum += SEP_CHAR;
            byte[] value_bytes = value.getBytes();
            for (byte character : value_bytes) {
                sum += character;
            }

            // Ajout du séparateur entre la valeur et le checksum
            sum += SEP_CHAR;

            return (byte)((sum & 0x3F) + 0x20);
        }
        else{
            return null;
        }
    }

    @Override
    protected void init() {
        FIRST_PRINTABLE_CHAR = 0x20;
        LAST_PRINTABLE_CHAR  = 0x7E;
        SEP_CHAR = 0x09; // Tab
    }
}
