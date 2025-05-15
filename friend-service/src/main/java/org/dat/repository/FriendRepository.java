package org.dat.repository;

import org.dat.entity.Friend;
import org.dat.enums.FriendRequestDirection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FriendRepository extends JpaRepository<Friend, UUID> {
    Optional<Friend> findByUserIdAndFriendId(UUID userId, UUID friendId);

    @Query("select n from Friend n where n.deleted = false and n.userId=:userId and n.confirmed=true")
    List<Friend> findFriendByUserId(UUID userId);

    @Query("select n from Friend n where n.deleted = false and n.userId=:userId or n.friendId=:userId and n.confirmed=true")
    List<Friend> findUser(UUID userId);

    boolean existsByUserIdAndFriendIdAndConfirmedAndDeletedFalse(UUID userId, UUID friendId, boolean b);

    List<Friend> findByUserIdAndConfirmedTrueAndDeletedFalse(UUID userId);

    List<Friend> findByFriendIdAndConfirmedFalseAndDeletedFalse(UUID friendId);

    List<Friend> findByUserIdAndDirectionAndConfirmedFalseAndDeletedFalse (UUID receiverId, FriendRequestDirection FriendRequestDirection);

}
