package domain.ticdataset;

import domain.exceptions.TicChecksumException;
import domain.exceptions.TicInvalidFormatException;

import java.time.LocalDateTime;

import static domain.toolbox.compute.Sum.sumBytes;

public class TicHistoDataSet extends TicDataSet{



    public TicHistoDataSet() {
        super();
    }

    public TicHistoDataSet(String label) {
        super(label);
    }

    public TicHistoDataSet(String label, String value) throws TicInvalidFormatException {
        super(label, value);
    }

    public TicHistoDataSet(String label, String value, LocalDateTime timestamp) throws TicChecksumException, TicInvalidFormatException {
        super(label, value, timestamp);
    }

    public TicHistoDataSet(String label, String value, LocalDateTime timestamp, Byte checksum) throws TicChecksumException, TicInvalidFormatException {
        super(label, value, timestamp, checksum);
    }

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
    public byte[] getBytes() {

        byte[] bytes = new byte[0];
        byte[] startOfDataset = new byte[]{0x0A}; // LF
        byte[] endOfDataSet = new byte[]{0x0D}; // CR

        //for(ByteArray byte)
        return new byte[0];
    }

    @Override
    protected void init(){
        FIRST_PRINTABLE_CHAR = 0x21;
        LAST_PRINTABLE_CHAR  = 0x7E;
        SEP_CHAR = 0x20; // Space
    }
}
