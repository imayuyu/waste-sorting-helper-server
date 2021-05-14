package com.charliechiang.wastesortinghelperserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.charliechiang.wastesortinghelperserver.exception.ResourceNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ServerEndpoint("/api/v1/ws/dustbins/{dustbinId}")
@Component
public class WebSocketController {
    private static final Log logger = LogFactory.getLog(WebSocketController.class);
    private static final AtomicInteger onlineDustbinCount = new AtomicInteger(0);
    private static final ConcurrentHashMap<Long, WebSocketController> connectionMap = new ConcurrentHashMap<>();
    private Session session;
    private Long dustbinId;
    private final LinkedHashMap<Long, ServerRequest> messageHistory = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, ServerRequest> eldest) {
            return size() > 10;
        }
    };

    @OnOpen
    public void onOpen(Session session,
                       @PathParam("dustbinId") Long dustbinId) {

        this.session = session;
        this.dustbinId = dustbinId;

        if (connectionMap.containsKey(dustbinId)) {
            connectionMap.remove(dustbinId);
            connectionMap.put(dustbinId, this);
        } else {
            connectionMap.put(dustbinId, this);
            onlineDustbinCount.incrementAndGet();
        }

        logger.info("Dustbin " + dustbinId + " connected. Connection count=" + onlineDustbinCount.get());
    }

    @OnClose
    public void onClose() {

        if (connectionMap.containsKey(dustbinId)) {
            connectionMap.remove(dustbinId);
            onlineDustbinCount.decrementAndGet();
        }

        logger.info("Dustbin " + dustbinId + " disconnected. Connection count=" + onlineDustbinCount.get());
    }

    @OnMessage
    public void onMessage(String message, Session session) {

        Integer type = null;
        Long requestId = null;
        String username = null;
        Long dustbinId = null;
        String description = null;

        if (!message.isEmpty()) {
            try {
                JSONObject jsonObject = JSON.parseObject(message);
                type = jsonObject.getInteger("type");
                requestId = jsonObject.getLong("requestId");
                username = jsonObject.getString("username");
                dustbinId = jsonObject.getLong("dustbinId");
                description = jsonObject.getString("description");
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }
        }


        if (type == null || requestId == null || username == null || dustbinId == null || description == null) {
            return;
        }

        ServerRequest serverRequest = new ServerRequest(type, requestId, username, dustbinId, description);

        messageHistory.remove(serverRequest.getRequestId());
        messageHistory.put(serverRequest.getRequestId(), serverRequest);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {

        logger.error("Connection error: dustbinId=" + this.dustbinId + ", message=" + throwable.getMessage());
        throwable.printStackTrace();
    }


    public static void sendRequest(ServerRequest serverRequest) throws ResourceNotFoundException, IOException {

        if (!connectionMap.containsKey(serverRequest.getDustbinId())) {
            throw new ResourceNotFoundException("Dustbin with ID=" + serverRequest.getDustbinId() + " could not be connected.");
        }

        WebSocketController referencedDustbinWS = connectionMap.get(serverRequest.getDustbinId());
        referencedDustbinWS.session.getBasicRemote().sendText(JSON.toJSONString(serverRequest));

        referencedDustbinWS.messageHistory.remove(serverRequest.getRequestId());
        referencedDustbinWS.messageHistory.put(serverRequest.getRequestId(), serverRequest);

    }

    public static ServerRequest getRequest(Long dustbinId, Long requestId) throws ResourceNotFoundException {

        if (!connectionMap.containsKey(dustbinId)) {
            throw new ResourceNotFoundException("Dustbin with ID=" + dustbinId + " could not be found.");
        }

        WebSocketController referencedDustbinWS = connectionMap.get(dustbinId);

        if (!referencedDustbinWS.messageHistory.containsKey(requestId)) {
            throw new ResourceNotFoundException("Request with ID=" + requestId + " could not be found.");
        }

        return referencedDustbinWS.messageHistory.get(requestId);
    }
}


class ServerRequest {
    //            | Key  | Description                           |
    //            | ---- | ------------------------------------- |
    //            | 0    | request has been successfully handled |
    //            | 1    | request cannot be fulfilled           |
    //            | 2    | request has not been processed        |
    private Integer type;
    private Long requestId;
    private String username;
    private Long dustbinId;
    private String description;

    public static ServerRequest generateNewRequest(String username,
                                                   Long dustbinId) {

        return new ServerRequest(2,
                                 System.currentTimeMillis(),
                                 username,
                                 dustbinId,
                                 "(Auto-generated) Lid-open");
    }

    public ServerRequest(Integer type,
                         Long requestId,
                         String username,
                         Long dustbinId,
                         String description) {

        this.type = type;
        this.requestId = requestId;
        this.username = username;
        this.dustbinId = dustbinId;
        this.description = description;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getDustbinId() {
        return dustbinId;
    }

    public void setDustbinId(Long dustbinId) {
        this.dustbinId = dustbinId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}