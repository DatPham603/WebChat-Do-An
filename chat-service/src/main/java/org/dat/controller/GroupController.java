package org.dat.controller;

import lombok.extern.slf4j.Slf4j;
import org.dat.config.UserPrincipal;
import org.dat.dto.dto.Response;
import org.dat.dto.dto.UserDTO;
import org.dat.dto.request.AddUserToGroupRequest;
import org.dat.dto.request.CreateGroupRequest;
import org.dat.entity.Group;
import org.dat.feignConfig.IAMServiceClient;
import org.dat.service.GroupService;
import org.dat.service.LocalStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups")
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class GroupController {
    private final GroupService groupService;
    private final IAMServiceClient iamServiceClient;
    private final SimpMessagingTemplate messagingTemplate;
    private final LocalStorageService storageService;

    public GroupController(GroupService groupService,
                           IAMServiceClient iamServiceClient,
                           SimpMessagingTemplate messagingTemplate,
                           LocalStorageService storageService) {
        this.groupService = groupService;
        this.iamServiceClient = iamServiceClient;
        this.messagingTemplate = messagingTemplate;
        this.storageService = storageService;
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

    @GetMapping("/get-group-users/{groupId}")
    public Response<List<UserDTO>> getGroupUsers(@PathVariable("groupId") UUID groupId) {
        return Response.of(groupService.getUsersFromGroup(groupId));
    }

    @PostMapping("/upload-group-avatar/")
    public ResponseEntity<Map<String, String>> uploadGroupAvatar(@RequestParam("image") MultipartFile image) {
        try {
            String imageUrl = storageService.storeGroupAvatarImage(image);
            return ResponseEntity.ok(Map.of("url", imageUrl));
        } catch (Exception e) {
            log.error("Lỗi khi tải lên ảnh: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error",
                    "Không thể tải lên ảnh."));
        }
    }

    @GetMapping(value = "/get-group-avatar/{filename}",
            produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getGroupAvatar(@PathVariable String filename) throws IOException {
        Path imagePath = Paths.get("./uploads/group_avatar/" + filename);
        if (!Files.exists(imagePath)) {
            throw new IOException("Không tìm thấy avatar nhóm: " + filename);
        }
        return Files.readAllBytes(imagePath);
    }

}
