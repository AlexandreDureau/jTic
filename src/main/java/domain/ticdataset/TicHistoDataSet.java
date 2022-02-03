package domain.ticdataset;

import arrays.ByteArray;
import computing.Sum;
import domain.TicTimestamp;
import domain.exceptions.TicChecksumException;
import domain.exceptions.TicInvalidFormatException;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;

import computing.Sum.*;
import domain.exceptions.TicInvalidTimestampException;

import static constants.Constants.VERIFY_CHECKSUM;

public class TicHistoDataSet extends TicDataSet{

    public TicHistoDataSet() {
        super();
    }

    public TicHistoDataSet(ByteArray data, int consistencyCheck) throws TicInvalidFormatException, TicChecksumException, TicInvalidTimestampException {

        super(data);

        data.removeAt(0);
        data.removeAt(data.size() -1);

        List<ByteArray> datasetParts = data.split(SEP_CHAR);

        if(3 == datasetParts.size()) {

            this.setLabel(datasetParts.get(0).getBytes());
            this.setValue(datasetParts.get(1).getBytes());
            this.setChecksum(datasetParts.get(2).getBytes());

            if((consistencyCheck & VERIFY_CHECKSUM) > 0) {
                if (!checksum.equals(computeChecksum())) {
                    throw new TicChecksumException();
                }
            }
        }
        else if(4 == datasetParts.size()) {
            this.setLabel(datasetParts.get(0).getBytes());
            this.timestamp = new TicTimestamp(datasetParts.get(1).getBytes(), consistencyCheck);
            this.setValue(datasetParts.get(2).getBytes());
            this.setChecksum(datasetParts.get(3).getBytes());

            if((consistencyCheck & VERIFY_CHECKSUM) > 0) {
                if (!checksum.equals(computeChecksum())) {
                    throw new TicChecksumException();
                }
            }
        }
        else {
            throw new TicInvalidFormatException("Invalid number of parts in dataset : " + datasetParts.size() + " whereas expected is 3 or 4",-1);
        }
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
    public Byte computeChecksum() {
        if(null != label && null != value) {
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

            return (byte)((sum & 0x3F) + 0x20);
        }
        else {
            return null;
        }
    }


    @Override
    protected void init(){
        FIRST_PRINTABLE_CHAR = 0x21;
        LAST_PRINTABLE_CHAR  = 0x7E;
        SEP_CHAR = 0x20; // Space
    }


    protected void setLabel(byte[] bytes) throws TicInvalidFormatException {
        try {
            this.label = new String(bytes, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new TicInvalidFormatException(e.getMessage(),-1);
        }
    }


    protected void setValue(byte[] bytes) throws TicInvalidFormatException {
        try {
            this.value = new String(bytes, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new TicInvalidFormatException(e.getMessage(),-1);
        }
    }

    protected void setChecksum(byte[] bytes) throws TicInvalidFormatException {

        if(bytes.length == 1) {
            this.checksum = bytes[0];
        }
        else {
            throw new TicInvalidFormatException("Checksum should be one single byte",-1);
        }
    }
}
