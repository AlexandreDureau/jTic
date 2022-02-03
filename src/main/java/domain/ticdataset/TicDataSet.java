package domain.ticdataset;

import arrays.ByteArray;
import domain.TicTimestamp;
import domain.exceptions.TicChecksumException;
import domain.exceptions.TicInvalidFormatException;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

import static constants.Constants.TIC_DATASET_START_KEY;
import static constants.Constants.TIC_DATASET_STOP_KEY;

public abstract class TicDataSet {

    // CONSTANTES
    public static final Byte COMPUTE_CHECKSUM = null;

    // Attributs
    protected String label;
    protected String value;
    protected TicTimestamp timestamp = null; // Optionnel;
    protected Byte checksum;
    protected byte FIRST_PRINTABLE_CHAR = 0x21;
    protected byte LAST_PRINTABLE_CHAR  = 0x7E;
    protected byte SEP_CHAR             = 0x20; // Espace


    public TicDataSet(){
        this.init();
    }

    public TicDataSet(String label) {

        this.init();
        this.label = label;
    }

    public TicDataSet(ByteArray data) throws TicInvalidFormatException {

        this.init();

        if(!data.startsWith(TIC_DATASET_START_KEY)){
            throw new TicInvalidFormatException("DataSet <" + data.toString() + "> should start with " + TIC_DATASET_START_KEY , 0);
        }

        if(!data.endsWith(TIC_DATASET_STOP_KEY)){
            throw new TicInvalidFormatException("DataSet <" + data.toString() + "> should end with " + TIC_DATASET_STOP_KEY , data.size()-1);
        }
    }

    public TicDataSet(String label, String value) throws TicInvalidFormatException {

        this.init();
        setLabel(label);
        setValue(value);
        this.checksum = computeChecksum();
    }


    public TicDataSet(String label, String value, LocalDateTime timestamp) throws TicChecksumException, TicInvalidFormatException {

        this.init();
        set(label, value, timestamp, COMPUTE_CHECKSUM);
    }


    public TicDataSet(String label, String value, Byte checksum) throws TicChecksumException, TicInvalidFormatException {

        set(label, value, null, checksum);
    }


    public TicDataSet(String label, String value, LocalDateTime timestamp, Byte checksum) throws TicChecksumException, TicInvalidFormatException {

        set(label, value, timestamp, checksum);
    }

    /**
     * Allow to set a Tic DataSet
     * @param label (required)
     *              The label of the Tic DataSet
     * @param value (required)
     *              The value of the Tic DataSet
     * @param timestamp (optional)
     *                  A timestamp attached to the Tic DataSet
     * @param checksum (@Notnull) :
     *                 The checksum of the Tic DataSet. null (COMPUTE_CHECKSUM) if the checksum has to be auto computed.
     *                 Otherwise, use a String consistent with the checksum rules computation
     * @throws TicInvalidFormatException : this exception is thrown if an invalid format is detected
     * @throws TicChecksumException : this exception is thrown if the given checksum is not consistent
     */
    public void set(String label, String value, LocalDateTime timestamp, Byte checksum) throws TicInvalidFormatException, TicChecksumException {

        setLabel(label);
        setValue(value);

        if(COMPUTE_CHECKSUM == checksum){
            this.checksum = computeChecksum();
        }
        else{
            this.checksum = checksum;
            verifyChecksum(checksum);
        }
    }

    private void setLabel(String label) throws TicInvalidFormatException {

        this.checkValidity(label);
        this.label = label;
    }


    public String getLabel() {

        return label;
    }


    public void setValue(String value) throws TicInvalidFormatException {

        this.checkValidity(value);
        this.value = value;
    }


    public String getValue() {

        return value;
    }


    public Byte getChecksum() {

        return checksum;
    }


    public TicTimestamp getTimestamp() {

        return timestamp;
    }


    public abstract Byte computeChecksum();

    public void verifyChecksum(Byte expectedChecksum) throws TicChecksumException{
        if(expectedChecksum!= null){
            if(expectedChecksum != this.computeChecksum()){
                throw new TicChecksumException();
            }
        }
        else {
            throw new TicChecksumException();
        }
    }


    protected void checkValidity(String text) throws TicInvalidFormatException {
        int index = -1;
        if((null != text)&&(!text.isEmpty())){

            byte[] text_byte_array = text.getBytes();

            for(byte character : text_byte_array){
                index++;
                if(character < FIRST_PRINTABLE_CHAR || character > LAST_PRINTABLE_CHAR ) {
                    throw new TicInvalidFormatException(text, index);
                }
            }
        }
        else{
            throw new TicInvalidFormatException(text, index);
        }

    }

    public byte[] getBytes(){

        byte[] bytesArray = new byte[0];
        bytesArray= ByteArray.s_append(bytesArray, TIC_DATASET_START_KEY);
        bytesArray= ByteArray.s_append(bytesArray,label.getBytes());
        bytesArray= ByteArray.s_append(bytesArray,SEP_CHAR);
        if(null != timestamp){
            bytesArray= ByteArray.s_append(bytesArray,timestamp.toString().getBytes());
            bytesArray= ByteArray.s_append(bytesArray,SEP_CHAR);
        }
        bytesArray= ByteArray.s_append(bytesArray,value.getBytes());
        bytesArray= ByteArray.s_append(bytesArray,SEP_CHAR);
        bytesArray= ByteArray.s_append(bytesArray,checksum);

        bytesArray= ByteArray.s_append(bytesArray,TIC_DATASET_STOP_KEY);
        return bytesArray;
    }

    @Override
    public String toString(){

        String dataset_str = null;
        try {
            dataset_str = "<" + label + new String(new byte[]{SEP_CHAR}, "UTF-8");

            if(timestamp != null){
                dataset_str += timestamp.toString() + new String(new byte[] {SEP_CHAR}, "UTF-8");
            }
            dataset_str += value + new String(new byte[]{SEP_CHAR}, "UTF-8") + new String(new byte[]{checksum}, "UTF-8") + ">";

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return dataset_str;
    }

    protected abstract void init();
}
