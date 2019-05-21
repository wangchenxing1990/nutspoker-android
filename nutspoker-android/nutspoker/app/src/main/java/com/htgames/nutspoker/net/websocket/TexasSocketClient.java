package com.htgames.nutspoker.net.websocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * 客户端
 */
public class TexasSocketClient extends WebSocketClient {
    private CountDownLatch connectionOpenedLatch;
    private Map<String, String> openHandShakeFields;
    private String message;

    public TexasSocketClient(URI serverURI) {
        super(serverURI);
    }

    public TexasSocketClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
        connectionOpenedLatch = new CountDownLatch(1);
        openHandShakeFields = new HashMap<String, String>();
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        Iterator<String> it = serverHandshake.iterateHttpFields();
        while (it.hasNext()) {
            String key = it.next();
            System.out.printf("%s %s%n", key, serverHandshake.getFieldValue(key)); // TODO Remove this
            openHandShakeFields.put(key, serverHandshake.getFieldValue(key));
        }
        connectionOpenedLatch.countDown();
    }

    @Override
    public void onMessage(String s) {

    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
        connectionOpenedLatch.countDown();
    }
}
