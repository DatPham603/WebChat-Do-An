package org.dat.controller;

import lombok.RequiredArgsConstructor;
import org.dat.config.UserPrincipal;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class WebRtcController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/webrtc.offer")
    public void offer(@Payload Map<String, Object> payload, Principal principal) {
        String receiverId = (String) payload.get("receiverId");
        Map<String, Object> offer = (Map<String, Object>) payload.get("offer");
        messagingTemplate.convertAndSendToUser(receiverId, "/queue/webrtc/offer",
                Map.of("offer", offer, "callerId", ((UserPrincipal) principal).getUserId()));
    }

    @MessageMapping("/webrtc.answer")
    public void answer(@Payload Map<String, Object> payload, Principal principal) {
        String receiverId = (String) payload.get("receiverId");
        Map<String, Object> answer = (Map<String, Object>) payload.get("answer");
        messagingTemplate.convertAndSendToUser(receiverId, "/queue/webrtc/answer",
                Map.of("answer", answer));
    }

    @MessageMapping("/webrtc.icecandidate")
    public void iceCandidate(@Payload Map<String, Object> payload, Principal principal) {
        String receiverId = (String) payload.get("receiverId");
        Map<String, Object> iceCandidate = (Map<String, Object>) payload.get("iceCandidate");
        messagingTemplate.convertAndSendToUser(receiverId, "/queue/webrtc/icecandidate",
                Map.of("iceCandidate", iceCandidate));
    }

    @MessageMapping("/webrtc.reject")
    public void reject(@Payload Map<String, Object> payload, Principal principal) {
        String receiverId = (String) payload.get("receiverId");
        messagingTemplate.convertAndSendToUser(receiverId, "/queue/webrtc/reject",
                Map.of("callerId", ((UserPrincipal) principal).getUserId()));
    }
}
