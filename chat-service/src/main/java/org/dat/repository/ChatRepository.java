package org.dat.repository;

import org.dat.dto.ChatDTO;
import org.dat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<Chat, UUID> {
    List<Chat> findBySenderId(UUID senderId);

    @Query("select n from Chat n where n.deleted = false and n.senderId = :userId and n.receiverId = :receiverId or " +
            "n.senderId = :receiverId and n.receiverId = :userId order by n.createdDate")
    List<Chat> findChatByReceiverId(@Param("userId") UUID userId, @Param("receiverId") UUID receiverId);
}

