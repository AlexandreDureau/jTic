package infra.channels.serial;

import com.fazecast.jSerialComm.SerialPort;

public class TicSerialPortConstants {

    public static final int TIC_HISTO_BAUDRATE = 1200;
    public static final int TIC_HISTO_DATABITS = 7;
    public static final int TIC_HISTO_STOPBITS = 1;
    public static final int TIC_HISTO_PARITY = SerialPort.EVEN_PARITY;

    public static final int TIC_STANDARD_BAUDRATE = 9600;
    public static final int TIC_STANDARD_DATABITS = 7;
    public static final int TIC_STANDARD_STOPBITS = 1;
    public static final int TIC_STANDARD_PARITY = SerialPort.EVEN_PARITY;

    private TicSerialPortConstants() {};
}
