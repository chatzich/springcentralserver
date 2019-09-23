package com.centralserver;

import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AppStartupRunner implements ApplicationRunner {

    static public WebSocketClient client = null;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        client = new WebSocketClient();
        try
        {
            client.start();
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }

    }
}