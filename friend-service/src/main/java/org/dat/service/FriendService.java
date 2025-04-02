package org.dat.service;

import org.dat.dto.response.FriendDTO;
import org.dat.dto.response.Response;
import org.dat.dto.response.UserDTO;
import org.dat.entity.Friend;
import org.dat.feignConfig.IAMServiceClient;
import org.dat.repository.FriendRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FriendService {
    private final IAMServiceClient iamServiceClient;
    private final FriendRepository friendRepository;

    public FriendService(IAMServiceClient iamServiceClient,
                         FriendRepository friendRepository) {
        this.iamServiceClient = iamServiceClient;
        this.friendRepository = friendRepository;
    }

    public void addFriend(UUID userId, UUID friendId) {
        Response<UserDTO> user = iamServiceClient.getUserInforbyUserId(userId);
        Response<UserDTO> friend = iamServiceClient.getUserInforbyUserId(friendId);
        if (user != null && friend != null) {
            Optional<Friend> existingRequest1 = friendRepository.findByUserIdAndFriendId(userId, friendId);
            Optional<Friend> existingRequest2 = friendRepository.findByUserIdAndFriendId(friendId, userId);
            if (existingRequest1.isPresent() || existingRequest2.isPresent()) {
                throw new RuntimeException("Lời mời kết bạn đã được gửi trước đó!");
            }

            friendRepository.save(Friend.builder()
                    .userId(userId)
                    .friendId(friendId)
                    .confirmed(false)
                    .deleted(false)
                    .userName(user.getData().getUserName())
                    .friendName(friend.getData().getUserName())
                    .build());

            friendRepository.save(Friend.builder()
                    .userId(friendId)
                    .friendId(userId)
                    .confirmed(false)
                    .deleted(false)
                    .userName(friend.getData().getUserName())
                    .friendName(user.getData().getUserName())
                    .build());
        }
    }

    public void acceptFriendRequest(UUID userId, UUID friendId) {
        Optional<Friend> friendRequest1 = friendRepository.findByUserIdAndFriendId(userId, friendId);
        Optional<Friend> friendRequest2 = friendRepository.findByUserIdAndFriendId(friendId, userId);

        if (friendRequest1.isPresent() && friendRequest2.isPresent()) {
            Friend friend1 = friendRequest1.get();
            Friend friend2 = friendRequest2.get();

            Response<UserDTO> user = iamServiceClient.getUserInforbyUserId(userId);
            Response<UserDTO> friend = iamServiceClient.getUserInforbyUserId(friendId);

            if (user != null && friend != null) {
                friend1.setConfirmed(true);
                friend2.setConfirmed(true);
                friend1.setUserName(user.getData().getUserName());
                friend2.setFriendName(user.getData().getUserName());
                friend1.setFriendName(friend.getData().getUserName());
                friend2.setUserName(friend.getData().getUserName());

                friendRepository.save(friend1);
                friendRepository.save(friend2);
            } else {
                throw new RuntimeException("Không tìm thấy người dùng.");
            }

        } else {
            throw new RuntimeException("Không tìm thấy lời mời kết bạn.");
        }
    }

    public List<FriendDTO> getFriendByUserId(UUID userId) {

        List<Friend> friends = friendRepository.findFriendByUserId(userId);

        return friends.stream().map(friend -> FriendDTO.builder()
                .friendId(friend.getFriendId())
                .friendName(friend.getFriendName())
                .userId(friend.getUserId())
                .build()).toList();

    }

    // API để cập nhật tên người dùng trong bảng Friend
    public void updateFriendNames(UUID userId, String userName) {
        List<Friend> friends = friendRepository.findFriendByUserId(userId);
        friends.forEach(friend -> {
            if (friend.getUserId().equals(userId)) {
                friend.setUserName(userName);
            } else {
                friend.setFriendName(userName);
            }
            friendRepository.save(friend);
        });
    }
}