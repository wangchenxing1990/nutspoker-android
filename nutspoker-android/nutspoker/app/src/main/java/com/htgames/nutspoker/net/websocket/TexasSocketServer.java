package com.htgames.nutspoker.net.websocket;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

/**
 */
public class TexasSocketServer extends WebSocketServer {
    public TexasSocketServer(InetSocketAddress address) {
        super(address);
    }

    public TexasSocketServer(InetSocketAddress address, int decoders) {
        super(address, decoders);
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {

    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {

    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {

    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }
}
