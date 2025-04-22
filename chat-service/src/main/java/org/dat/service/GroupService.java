package org.dat.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.dat.dto.dto.Response;
import org.dat.dto.dto.UserDTO;
import org.dat.entity.Group;
import org.dat.entity.GroupMember;
import org.dat.enums.Role;
import org.dat.feignConfig.IAMServiceClient;
import org.dat.repository.GroupMemberRepository;
import org.dat.repository.GroupRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class GroupService {
    private final GroupMemberRepository groupMemberRepository;
    private final GroupRepository groupRepository;
    private final IAMServiceClient iamServiceClient;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public GroupService(GroupMemberRepository groupMemberRepository,
                        GroupRepository groupRepository,
                        IAMServiceClient iamServiceClient,
                        SimpMessagingTemplate simpMessagingTemplate) {
        this.groupMemberRepository = groupMemberRepository;
        this.groupRepository = groupRepository;
        this.iamServiceClient = iamServiceClient;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void checkGroupOwnership(String groupId, String userId) {
        Optional<Group> groupOptional = groupRepository.findById(UUID.fromString(groupId));
        if (groupOptional.isEmpty() || !groupOptional.get().getOwnerId().equals(UUID.fromString(userId))) {
            throw new SecurityException("You are not the owner of this group.");
        }
    }

    @Transactional
    public void addUserToGroup(String groupId, String userEmailToAdd, String ownerId) {
        checkGroupOwnership(groupId, ownerId);

        Response<UserDTO> userToAdd = iamServiceClient.getUserInforbyEmail(userEmailToAdd);
        Group group = groupRepository.findById(UUID.fromString(groupId))
                .orElseThrow(() -> new IllegalArgumentException("Group not found."));

        if (groupMemberRepository.existsByGroupIdAndUserId(group.getId(), userToAdd.getData().getId())) {
            throw new IllegalArgumentException("User is already in the group.");
        }

        GroupMember newMember = GroupMember.builder()
                .groupId(UUID.fromString(groupId))
                .userId(userToAdd.getData().getId())
                .role(Role.MEMBER)
                .build();
        groupMemberRepository.save(newMember);

    }


    public Group createGroup(String name, UUID ownerId, List<UUID> initialMemberIds) {
        Group newGroup = Group.builder()
                .name(name)
                .ownerId(ownerId)
                .build();
        newGroup = groupRepository.save(newGroup);

        // Thêm người tạo nhóm vào danh sách thành viên
        GroupMember ownerMember = GroupMember.builder()
                .groupId(newGroup.getId())
                .userId(ownerId)
                .role(Role.ADMIN)
                .build();
        groupMemberRepository.save(ownerMember);

        // Thêm các thành viên ban đầu (nếu có)
        if (initialMemberIds != null && !initialMemberIds.isEmpty()) {
            for (UUID memberId : initialMemberIds) {
                // Kiểm tra xem người dùng có tồn tại không trước khi thêm (tùy chọn)
                try {
                    Response<UserDTO> userResponse = iamServiceClient.getUserInforbyUserId(memberId);
                    if (userResponse != null && userResponse.getData() != null) {
                        GroupMember member = GroupMember.builder()
                                .groupId(newGroup.getId())
                                .userId(memberId)
                                .role(Role.MEMBER)
                                .build();
                        groupMemberRepository.save(member);
                    } else {
                        log.warn("User with ID {} not found, skipping adding to group {}", memberId, newGroup.getId());
                    }
                } catch (Exception e) {
                    log.error("Error while checking user with ID {}: {}", memberId, e.getMessage());
                    // Quyết định có nên bỏ qua lỗi hay không
                }
            }
        }

        return newGroup;
    }

    public List<UserDTO> getUsersFromGroup(UUID groupId) {
        Group group = groupRepository.findById(groupId).get();
        if(group == null) {
            throw new IllegalArgumentException("Group not found.");
        }
        List<UUID> userId = groupMemberRepository.findUserIdByGroupId(groupId);
        return iamServiceClient.getUsersByIds(userId).getData();
    }
}
