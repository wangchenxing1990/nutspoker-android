package com.htgames.nutspoker.net.websocket;

import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ClientHandshakeBuilder;

public class Draft17WithOrigin extends Draft_17 {

    public Draft Draft17WithOrigin() {
        return new Draft17WithOrigin();
    }

    @Override
    public ClientHandshakeBuilder postProcessHandshakeRequestAsClient(ClientHandshakeBuilder request) {
        super.postProcessHandshakeRequestAsClient(request);
//            request.put("Origin", myURI.toString());
        return request;
    }
}