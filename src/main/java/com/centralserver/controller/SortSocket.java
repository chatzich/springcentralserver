
package com.centralserver.controller;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

/**
 * Basic Echo Client Socket
 */
@WebSocket(maxTextMessageSize = 64 * 1024)
public class SortSocket
{
    private String message;
    private String response = null;

    private final CountDownLatch closeLatch;
    @SuppressWarnings("unused")
    private Session session;

    void setMessage(String message) {
        this.message = message;
    }

    String getRespose() {
        return this.response;
    }

    public SortSocket()
    {
        this.closeLatch = new CountDownLatch(1);
    }

    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException
    {
        try {
            while(this.response == null) {
                this.wait(10);
            }

        } catch (Throwable t)
        {

        }
        return true;
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason)
    {
        System.out.printf("Connection closed: %d - %s%n", statusCode, reason);
        this.session = null;
        this.closeLatch.countDown(); // trigger latch
    }

    @OnWebSocketConnect
    public void onConnect(Session session)
    {
        System.out.printf("Got connect: %s%n", session);
        this.session = session;
        try
        {
            Future<Void> fut;
            fut = session.getRemote().sendStringByFuture(this.message);
            fut.get(2, TimeUnit.SECONDS); // wait for send to complete.
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }

    @OnWebSocketMessage
    public void onMessage(String msg)
    {
        System.out.printf("Got message:" + msg);

        this.response = msg;
    }

    @OnWebSocketError
    public void onError(Throwable cause)
    {
        System.out.print("WebSocket Error: ");
        cause.printStackTrace(System.out);
    }
}