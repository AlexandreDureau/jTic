package domain.ticdataset;

import arrays.ByteArray;
import computing.Sum;

public class TicStandardDataSet extends TicDataSet{

    @Override
    public Byte computeChecksum() {

        if(null != label && null != value){
            int sum=0;

            // Ajout des octets du label
            sum += Sum.sumBytesOnByte(label);

            // Ajout des octets du timestamp
            if(null != timestamp){
                sum += SEP_CHAR;
                sum += Sum.sumBytesOnByte(timestamp.get());
            }

            // Ajout des octets de la valeur
            sum += SEP_CHAR;
            sum += Sum.sumBytesOnByte(value);

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
