//package org.dat.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import java.io.IOException;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Slf4j
//@Component
//public class ChatWebSocketHandler extends TextWebSocketHandler {
//
//    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) {
//        String userId = (String) session.getAttributes().get("userId");
//        log.info("userId = " + userId);
//        if (userId != null) {
//            sessions.put(userId, session);
//            log.info("WebSocket connection established for user: {}", userId);
//        } else {
//            try {
//                session.close(CloseStatus.POLICY_VIOLATION);
//            } catch (IOException e) {
//                log.error("Error closing session", e);
//            }
//        }
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
//        String userId = session.getHandshakeHeaders().getFirst("X-User-Id");
//        if (userId != null) {
//            sessions.remove(userId);
//            log.info("WebSocket connection closed for user: {}", userId);
//        }
//    }
//
//    @Override
//    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        String userId = session.getHandshakeHeaders().getFirst("X-User-Id");
//        if (userId != null) {
//            String payload = message.getPayload();
//            log.info("Received message from user {}: {}", userId, payload);
//
//            // Xử lý tin nhắn và gửi lại cho các người dùng khác
//            // Ví dụ: gửi lại cho tất cả các session khác
//            for (WebSocketSession otherSession : sessions.values()) {
//                if (otherSession != session && otherSession.isOpen()) {
//                    otherSession.sendMessage(new TextMessage("User " + userId + ": " + payload));
//                }
//            }
//        }
//    }
//}
