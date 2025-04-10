package org.dat.service;

import lombok.RequiredArgsConstructor;
import org.dat.dto.dto.ChatListItemDTO;
import org.dat.dto.dto.UserDTO;
import org.dat.entity.Chat;
import org.dat.entity.Group;
import org.dat.entity.GroupMember;
import org.dat.enums.MessageType;
import org.dat.feignConfig.IAMServiceClient;
import org.dat.repository.ChatRepository;
import org.dat.repository.GroupMemberRepository;
import org.dat.repository.GroupRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatListService {

    private final ChatRepository chatRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final IAMServiceClient userService; // Inject UserService

    public List<ChatListItemDTO> getChatList(UUID currentUserId) {
        List<ChatListItemDTO> chatList = new ArrayList<>();
        Set<UUID> chattedUserIds = new HashSet<>();

        // Lấy danh sách người đã chat cùng (tin nhắn cá nhân)
        List<Chat> userChats = chatRepository.findBySenderIdOrReceiverIdAndType(currentUserId, MessageType.CHAT);

        for (Chat chat : userChats) {
            UUID otherUserId = null;
            if (chat.getSenderId().equals(currentUserId)) {
                otherUserId = chat.getReceiverId();
            } else {
                otherUserId = chat.getSenderId();
            }

            if (otherUserId != null && !chattedUserIds.contains(otherUserId)) {
                chattedUserIds.add(otherUserId);
                try {
                    UserDTO user = userService.getUserInforbyUserId(otherUserId).getData(); // Sử dụng UserService để lấy thông tin người dùng
                    if (user != null) {
                        Chat lastMessage = chatRepository.findTopBySenderIdAndReceiverIdOrSenderIdAndReceiverIdAndTypeOrderByCreatedDateDesc(
                                currentUserId, otherUserId, MessageType.CHAT
                        ).orElse(null);

                        ChatListItemDTO dto = new ChatListItemDTO();
                        dto.setType("user");
                        dto.setId(user.getId());
                        dto.setName(user.getUserName());
                        dto.setEmail(user.getEmail());
                        dto.setLastMessage(lastMessage != null ? lastMessage.getContent() : "No messages yet");
                        dto.setLastActive(lastMessage != null ? lastMessage.getCreatedDate() : null);
                        chatList.add(dto);
                    }
                } catch (Exception e) {
                    // Xử lý trường hợp không tìm thấy user hoặc lỗi gọi IAM service
                    // Có thể log lỗi hoặc bỏ qua user này nếu cần
                    System.err.println("Error fetching user info for ID: " + otherUserId + " - " + e.getMessage());
                }
            }
        }

        // Lấy danh sách các nhóm mà người dùng là thành viên
        List<GroupMember> groupMembers = groupMemberRepository.findByUserId(currentUserId);

        for (GroupMember member : groupMembers) {
            Group group = groupRepository.findById(member.getGroupId()).orElse(null);
            if (group != null) {
                Chat lastMessage = chatRepository.findTopByReceiverIdAndTypeOrderByCreatedDateDesc(
                        group.getId(), MessageType.GROUP_CHAT // Sử dụng MessageType.GROUP_CHAT để lọc tin nhắn nhóm
                ).orElse(null);

                ChatListItemDTO dto = new ChatListItemDTO();
                dto.setType("group");
                dto.setId(group.getId());
                dto.setName(group.getName());
                long memberCount = groupMemberRepository.countByGroupId(group.getId());
                dto.setMemberCount((int) memberCount);
                dto.setLastMessage(lastMessage != null ? lastMessage.getContent() : "No messages yet");
                dto.setLastActive(lastMessage != null ? lastMessage.getCreatedDate() : null);
                chatList.add(dto);
            }
        }

        // Sắp xếp theo thời gian hoạt động cuối cùng (nếu có)
        chatList.sort((item1, item2) -> {
            Instant time1 = item1.getLastActive();
            Instant time2 = item2.getLastActive();
            if (time1 == null && time2 == null) return 0;
            if (time1 == null) return 1;
            if (time2 == null) return -1;
            return time2.compareTo(time1);
        });

        return chatList;
    }
}