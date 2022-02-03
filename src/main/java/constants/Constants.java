package constants;

public class Constants {
    public static final byte TIC_FRAME_START_KEY = 2; // STX
    public static final byte TIC_FRAME_STOP_KEY  = 3; // ETX
    public static final byte TIC_DATASET_START_KEY= 10; // LF
    public static final byte TIC_DATASET_STOP_KEY= 13; // CR


    public static final int NO_CONSISTENCY_CHECK = 0;
    public static final int VERIFY_CHECKSUM      = 1;
    public static final int VERIFY_TIMESTAMP     = 2;
    public static final int CHECK_ALL            = VERIFY_CHECKSUM | VERIFY_TIMESTAMP;
}
