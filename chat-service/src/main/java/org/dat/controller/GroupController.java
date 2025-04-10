package org.dat.controller;

import org.dat.config.UserPrincipal;
import org.dat.dto.dto.Response;
import org.dat.dto.dto.UserDTO;
import org.dat.dto.request.AddUserToGroupRequest;
import org.dat.dto.request.CreateGroupRequest;
import org.dat.entity.Group;
import org.dat.feignConfig.IAMServiceClient;
import org.dat.service.GroupService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups")
@CrossOrigin(origins = "http://localhost:4200")
public class GroupController {
    private final GroupService groupService;
    private final IAMServiceClient iamServiceClient;
    private final SimpMessagingTemplate messagingTemplate; // Thêm


    public GroupController(GroupService groupService,
                           IAMServiceClient iamServiceClient,
                           SimpMessagingTemplate messagingTemplate) {
        this.groupService = groupService;
        this.iamServiceClient = iamServiceClient;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/create-group")
    public Response<Group> createGroup(
            @RequestBody CreateGroupRequest createGroupRequest) {
            Group newGroup = groupService.createGroup(
                    createGroupRequest.getName(),
                    UUID.fromString(createGroupRequest.getOwnerId()),
                    createGroupRequest.getMemberIds()
            );

        if (createGroupRequest.getMemberIds() != null && !createGroupRequest.getMemberIds().isEmpty()) {
            messagingTemplate.convertAndSend(
                    "/topic/groups/" + newGroup.getId(),
                    String.format("Group '%s' created with initial members.", newGroup.getName())
            );
        }
            return Response.of(newGroup);
    }

    @PostMapping("/{groupId}/add-user")
    public Response<Void> addUserToGroup(
            @PathVariable String groupId,
            @RequestBody AddUserToGroupRequest addUserRequest) {
        groupService.addUserToGroup(groupId, addUserRequest.getEmail(), addUserRequest.getOwnerId());
        messagingTemplate.convertAndSend(
                "/topic/groups/" + groupId,
                String.format("User with email %s added to group.", addUserRequest.getEmail())
        );

        return Response.ok();
    }

    @GetMapping("/{groupId}/find-user-by-email")
    public Response<UserDTO> findUserByEmail(
            @RequestParam String email,
            UserPrincipal principal) {
        try {
            Response<UserDTO> user = iamServiceClient.getUserInforbyEmail(email);
            return Response.of(user.getData());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Something went wrong", e);
        }
    }
}
