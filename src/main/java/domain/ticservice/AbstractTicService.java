package domain.ticservice;

import domain.exceptions.TicInvalidConfigException;
import infra.channels.TicChannel;
import infra.channels.TicRxChannelFactory;
import org.json.JSONException;
import org.json.JSONObject;
import types.TicMode;

import java.io.*;

public abstract class AbstractTicService implements TicService{

    protected TicMode ticMode = TicMode.HISTO;

    public void set(Object config) throws TicInvalidConfigException {

        if(config instanceof String){
            this.setConfigFromString((String) config);
        }
        else if(config instanceof File){
            this.setConfigFromFile((File) config);
        }
        else if(config instanceof JSONObject){
            this.setConfigFromJSON((JSONObject) config);
        }
        else{
            throw new TicInvalidConfigException("configuration is expected to be JSONObject or JSON string or JSON file path and not <" + config.getClass().getSimpleName() + ">");
        }
    }

    private void setConfigFromString(String config) throws TicInvalidConfigException {

        try{
            // 1st case : does the String represent a JSON ?
            JSONObject jsonObject = new JSONObject(config);
            this.setConfigFromJSON(jsonObject);
        }
        catch (JSONException jsonException){
            // 2nd case : does the String represent a file path?
            File file = new File(config);
            if(file.exists()){
                this.setConfigFromFile(file);
            }
            else{
                throw new TicInvalidConfigException("Format of configuration <" + config + "> is not handled");
            }
        }
    }


    private void setConfigFromFile(File file) throws TicInvalidConfigException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            StringBuilder config_str= new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                config_str.append(line);
            }

            this.setConfigFromString(config_str.toString());
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void setConfigFromJSON(JSONObject jsonObject) throws TicInvalidConfigException {

        try{
            this.setTicMode(jsonObject.getString("ticMode"));

            // NB: the channel need all the config, included the ticMode to be configured
            this.setTicChannel(jsonObject);
        }
        catch(JSONException jsonException){
            throw new TicInvalidConfigException(jsonException.getMessage());
        }
    }


    private void setTicMode(String ticMode) throws TicInvalidConfigException {
        switch (ticMode.trim().toLowerCase()){
            case "histo"    : this.ticMode = TicMode.HISTO; break;
            case "standard" : this.ticMode = TicMode.STANDARD; break;
            default : throw new TicInvalidConfigException("TIC mode <" + ticMode + "> is not valid");
        }
    }

    protected abstract void setTicChannel(JSONObject config) throws TicInvalidConfigException;
}
