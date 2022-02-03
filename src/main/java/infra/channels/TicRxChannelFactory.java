package infra.channels;

import domain.exceptions.TicInvalidConfigException;
import domain.exceptions.TicInvalidFormatException;
import infra.channels.file.TicFileRxChannel;
import infra.channels.serial.TicSerialPortRxChannel;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

public class TicRxChannelFactory {

    public static TicRxChannel buildChannel(Object config) throws TicInvalidConfigException {
        TicRxChannel ticRxChannel = null;

        if(config instanceof String){
            ticRxChannel = buildChannelFromString((String) config);
        }
        else if(config instanceof File){
            try {
                ticRxChannel = buildChannelFromFile((File) config);
            }
            catch (IOException exception) {
                throw new TicInvalidConfigException("Impossible to get configuration", exception);
            }
        }
        else if(config instanceof JSONObject){
            ticRxChannel = buildChannelFromJSON((JSONObject) config);
        }

        else{
            throw new TicInvalidConfigException("configuration is expected to be JSONObject or JSON string or JSON file path and not <" + config.getClass().getSimpleName() + ">");
        }

        return ticRxChannel;
    }


    private static TicRxChannel buildChannelFromString(String configString) throws TicInvalidConfigException {
        TicRxChannel ticRxChannel = null;

        configString = configString.trim();

        if(isFilePath(configString)){
            try{
                ticRxChannel = buildChannelFromFile(new File(configString));
            }
            catch (IOException exception){
                throw new TicInvalidConfigException(exception.getMessage());
            }
        }
        else if(isJSON(configString)){
            ticRxChannel = buildChannelFromJSON(new JSONObject(configString));
        }
        else{
            throw new TicInvalidConfigException("<" + "> has invalid format ; JSON String or JSON file path expected");
        }
        return ticRxChannel;
    }

    private static TicRxChannel buildChannelFromFile(File configFile) throws IOException, TicInvalidConfigException {

        BufferedReader fileReader = new BufferedReader(new FileReader(configFile));
        StringBuilder content = new StringBuilder();
        String line  = "";
        while(null != line)
        {
            line = fileReader.readLine();
            if(null != line)
            {
                content.append(line.trim());
            }
        }

        return buildChannelFromString(content.toString());
    }


    private static TicRxChannel buildChannelFromJSON(JSONObject configJSON) throws TicInvalidConfigException {
        TicRxChannel ticRxChannel = null;

        try{
            String type  = configJSON.getString("type").trim().toLowerCase();

            switch (type){
                case "serial"     :
                case "serialport" : ticRxChannel = (TicRxChannel) new TicSerialPortRxChannel(configJSON); break;
                case "udp"        : /* TODO */break;
                case "tcp"        : /* TODO */break;
                case "websocket"  : /* TODO */break;
                case "file"       : ticRxChannel = (TicRxChannel) new TicFileRxChannel(configJSON); break;
                default           : throw new TicInvalidConfigException("type " + type + " is not handled");
            }
        }

        catch (JSONException exception){
            throw new TicInvalidConfigException("", exception);
        }

        return ticRxChannel;
    }

    private static boolean isFilePath(String str){
        File file = new File(str);
        return file.exists() && file.isFile();
    }

    private static boolean isJSON(String str){
        try{
            JSONObject json = new JSONObject(str);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
}
