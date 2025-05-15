package org.dat.service;

import org.dat.dto.response.FriendDTO;
import org.dat.dto.response.FriendRequestDTO;
import org.dat.dto.response.Response;
import org.dat.dto.response.UserDTO;
import org.dat.entity.Friend;
import org.dat.enums.FriendRequestDirection;
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
                    .direction(FriendRequestDirection.SENT)
                    .userName(user.getData().getUserName())
                    .friendName(friend.getData().getUserName())
                    .email(user.getData().getEmail())
                    .friendEmail(friend.getData().getEmail())
                    .build());

            friendRepository.save(Friend.builder()
                    .userId(friendId)
                    .friendId(userId)
                    .confirmed(false)
                    .deleted(false)
                    .direction(FriendRequestDirection.RECEIVED)
                    .userName(friend.getData().getUserName())
                    .friendName(user.getData().getUserName())
                    .email(friend.getData().getEmail())
                    .friendEmail(user.getData().getEmail())
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
                friend2.setUserName(friend.getData().getUserName());
                friend1.setFriendName(friend.getData().getUserName());
                friend2.setFriendName(user.getData().getUserName());
                friend1.setEmail(user.getData().getEmail());
                friend2.setEmail(friend.getData().getEmail());
                friend1.setFriendEmail(friend.getData().getEmail());
                friend2.setFriendEmail(user.getData().getEmail());

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
                .friendEmail(friend.getFriendEmail())
                .userId(friend.getUserId())
                .build()).toList();
    }

    public List<FriendDTO> getFriendByUserMail(String mail) {
        Response<UserDTO> user = iamServiceClient.getUserInforbyEmail(mail);

        List<Friend> friends = friendRepository.findFriendByUserId(user.getData().getId());

        return friends.stream().map(friend -> FriendDTO.builder()
                .friendId(friend.getFriendId())
                .friendName(friend.getFriendName())
                .friendEmail(friend.getFriendEmail())
                .userId(friend.getUserId())
                .confirmed(friend.isConfirmed())
                .build()).toList();
    }

    // API để cập nhật tên người dùng trong bảng Friend
    public void updateFriendNames(UUID userId, String userName) {
        List<Friend> friends = friendRepository.findUser(userId);
        friends.forEach(friend -> {
            if (friend.getUserId().equals(userId)) {
                friend.setUserName(userName);
            } else {
                friend.setFriendName(userName);
            }
            friendRepository.save(friend);
        });
    }

    public List<FriendRequestDTO> getFriendRequests(UUID receiverId) {
        List<Friend> friendRequests = friendRepository.findByUserIdAndDirectionAndConfirmedFalseAndDeletedFalse(
                receiverId, FriendRequestDirection.RECEIVED);

        return friendRequests.stream()
                .map(request -> {
                    UserDTO sender = iamServiceClient.getUserInforbyUserId(request.getFriendId()).getData();
                    return FriendRequestDTO.builder()
                            .receiverId(request.getUserId())
                            .senderId(request.getFriendId())
                            .senderName(request.getFriendName())
                            .senderEmail(request.getFriendEmail())
                            .senderAvatar(sender.getAvatar())
                            .requestId(request.getId())
                            .build();
                })
                .toList();
    }

    public void updateFriendEmails(UUID userId, String email) {
        List<Friend> friends = friendRepository.findUser(userId);
        friends.forEach(friend -> {
            if (friend.getUserId().equals(userId)) {
                friend.setEmail(email);
            } else {
                friend.setFriendEmail(email);
            }
            friendRepository.save(friend);
        });
    }

    public boolean areFriends(UUID userId, UUID friendId) {
        if(friendRepository.findByUserIdAndFriendId(userId, friendId).isEmpty() ||
                friendRepository.findByUserIdAndFriendId(userId, friendId).isEmpty()) {
            return false;
        }
        return friendRepository.existsByUserIdAndFriendIdAndConfirmedAndDeletedFalse(userId, friendId, true) ||
                friendRepository.existsByUserIdAndFriendIdAndConfirmedAndDeletedFalse(friendId, userId, true);
    }

    public List<FriendDTO> searchAllUserFriends(UUID userId) {
        List<Friend> friendsOfUser = friendRepository.findByUserIdAndConfirmedTrueAndDeletedFalse(userId);
        return friendsOfUser.stream().map(friend -> FriendDTO.builder()
                .friendId(friend.getFriendId())
                .friendName(friend.getFriendName())
                .friendEmail(friend.getFriendEmail())
                .userId(friend.getUserId())
                .build()).toList();
    }
}