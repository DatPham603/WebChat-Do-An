//package org.dat.config;
//
//import lombok.RequiredArgsConstructor;
//import org.dat.service.LocalStorageService;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.BinaryMessage;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.BinaryWebSocketHandler;
//
//import java.util.Map;
//
//@Component
//@RequiredArgsConstructor
//public class FileImageWebSocketHandler extends BinaryWebSocketHandler {
//
//    private final LocalStorageService localStorageService;
//    private final SimpMessagingTemplate messagingTemplate;
//
//    @Override
//    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
//        byte[] data = message.getPayload().array();
//        Map<String, Object> attributes = session.getAttributes();
//        String fileName = (String) attributes.get("fileName"); // Giả sử client gửi tên file trong attributes
//
//        if (fileName != null) {
//            String fileUrl;
//            if (fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".png")) {
//                fileUrl = localStorageService.storeImage(data);
//            } else {
//                fileUrl = localStorageService.storeFile(data, fileName);
//            }
//
//            // Gửi URL đến client
//            messagingTemplate.convertAndSendToUser((String) attributes.get("userId"),
//                    "/queue/messages", fileUrl);
//        } else {
//            // Xử lý trường hợp không có tên file
//            session.sendMessage(new TextMessage("Lỗi: Không có tên file."));
//        }
//    }
//}
