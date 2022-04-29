package domain.ticservice;

import domain.exceptions.TicInvalidConfigException;
import infra.channels.TicChannel;
import types.TicMode;

import java.io.IOException;

public interface TicService {

    void start() throws IOException;
    void stop() throws IOException;
    void set(Object config) throws TicInvalidConfigException;
}

