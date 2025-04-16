package org.dat.service;

import lombok.RequiredArgsConstructor;
import org.dat.dto.dto.ChatListItemDTO;
import org.dat.dto.dto.FriendDTO;
import org.dat.dto.dto.Response;
import org.dat.dto.dto.UserDTO;
import org.dat.entity.Chat;
import org.dat.entity.Group;
import org.dat.entity.GroupMember;
import org.dat.enums.MessageType;
import org.dat.feignConfig.FriendServiceClient;
import org.dat.feignConfig.IAMServiceClient;
import org.dat.repository.ChatRepository;
import org.dat.repository.GroupMemberRepository;
import org.dat.repository.GroupRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatListService {

    private final ChatRepository chatRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final IAMServiceClient userService;
    private final FriendServiceClient friendService;

    public List<ChatListItemDTO> getChatList(UUID currentUserId) {
        List<ChatListItemDTO> chatList = new ArrayList<>();
        Set<UUID> processedUserIds = new HashSet<>();

        Response<List<FriendDTO>> friendResponse = friendService.getListFriend(userService.getUserInforbyUserId(currentUserId).getData().getEmail());
        if (friendResponse.isSuccess() && friendResponse.getData() != null) {
            for (FriendDTO friend : friendResponse.getData()) {
                UUID friendId = friend.getFriendId();
                if (!processedUserIds.contains(friendId)) {
                    processedUserIds.add(friendId);
                    try {
                        Response<UserDTO> userResponse = userService.getUserInforbyUserId(friendId);
                        if (userResponse.isSuccess() && userResponse.getData() != null) {
                            UserDTO user = userResponse.getData();
                            Sort sortByCreatedDateDesc = Sort.by(Sort.Direction.DESC, "createdDate");
                            Specification<Chat> spec = (root, query, criteriaBuilder) -> criteriaBuilder.or(
                                    criteriaBuilder.and(
                                            criteriaBuilder.equal(root.get("senderId"), currentUserId),
                                            criteriaBuilder.equal(root.get("receiverId"), friendId),
                                            criteriaBuilder.equal(root.get("type"), MessageType.CHAT)
                                    ),
                                    criteriaBuilder.and(
                                            criteriaBuilder.equal(root.get("senderId"), friendId),
                                            criteriaBuilder.equal(root.get("receiverId"), currentUserId),
                                            criteriaBuilder.equal(root.get("type"), MessageType.CHAT)
                                    )
                            );
                            List<Chat> lastMessages = chatRepository.findAll(spec, sortByCreatedDateDesc);
                            Chat lastMessage = lastMessages.isEmpty() ? null : lastMessages.get(0);

                            ChatListItemDTO dto = new ChatListItemDTO();
                            dto.setType("user");
                            dto.setId(user.getId());
                            dto.setName(user.getUserName());
                            dto.setEmail(user.getEmail());
                            dto.setAvatar(user.getAvatar()); // Giả sử UserDTO có trường avatar
                            dto.setLastMessage(lastMessage != null ? lastMessage.getContent() : "No messages yet");
                            dto.setLastActive(lastMessage != null ? lastMessage.getCreatedDate() : null);
                            chatList.add(dto);
                        }
                    } catch (Exception e) {
                        System.err.println("Error fetching user info for friend ID: " + friendId + " - " + e.getMessage());
                    }
                }
            }
        }

        // 2. Lấy danh sách các nhóm mà người dùng là thành viên
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
//                dto.setAvatar(group.getAvarta);
                long memberCount = groupMemberRepository.countByGroupId(group.getId());
                dto.setMemberCount((int) memberCount);
                dto.setLastMessage(lastMessage != null ? lastMessage.getContent() : "No messages yet");
                dto.setLastActive(lastMessage != null ? lastMessage.getCreatedDate() : null);
                chatList.add(dto);
            }
        }

        // 3. Sắp xếp theo thời gian hoạt động cuối cùng (nếu có)
        chatList.sort(Comparator.comparing(ChatListItemDTO::getLastActive, Comparator.nullsLast(Comparator.reverseOrder())));

        return chatList;
    }
}