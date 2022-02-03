package infra.channels;

import domain.exceptions.TicInvalidConfigException;
import org.json.JSONObject;

import java.io.IOException;

public interface TicChannel {

    void start() throws IOException;
    void stop() throws IOException;
    boolean isStarted();
    void setConfig(JSONObject config) throws TicInvalidConfigException;
}
