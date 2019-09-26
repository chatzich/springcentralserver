package com.centralserver;

import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AppStartupRunner implements ApplicationRunner {

    static public WebSocketClient client = null;
    static private String destUri = "ws://localhost:1234";
    static public String getDestUri() {
        return destUri;
    }
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