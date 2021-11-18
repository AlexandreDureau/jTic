package common.compute;

public class sum {

    public static byte sumBytes(String text){

        int sum = 0;
        if(null != text) {
            byte[] bytesArray = text.getBytes();
            for(byte b : bytesArray){
                sum += b;
            }
        }
        return (byte) sum;
    }
}
