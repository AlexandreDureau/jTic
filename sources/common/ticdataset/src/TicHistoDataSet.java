package common.ticdataset.src;

import common.exceptions.TicChecksumException;
import common.exceptions.TicInvalidFormatException;

import static common.compute.sum.sumBytes;

public class TicHistoDataSet extends TicDataSet{

    @Override
    protected Byte computeChecksum() {
        if(null != label && null != value){
            int sum=0;

            // Ajout des octets du label
            sum += sumBytes(label);

            // Ajout des octets du timestamp
            if(null != timestamp){
                sum += SEP_CHAR;
                sum += sumBytes(timestamp.get());
            }

            // Ajout des octets de la valeur
            sum += SEP_CHAR;
            sum += sumBytes(value);

            return (byte)((sum & 0x3F) + 0x20);
        }
        else{
            return null;
        }
    }

    @Override
    protected void init(){
        FIRST_PRINTABLE_CHAR = 0x21;
        LAST_PRINTABLE_CHAR  = 0x7E;
        SEP_CHAR = 0x20; // Space
    }
}
