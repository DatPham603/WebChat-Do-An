package org.dat.repository;

import org.dat.entity.Chat;
import org.dat.enums.ContentType;
import org.dat.enums.MessageType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<Chat, UUID> {
    List<Chat> findBySenderId(UUID senderId);

    @Query("select n from Chat n where n.deleted = false and n.senderId = :userId and n.receiverId = :receiverId or " +
            "n.senderId = :receiverId and n.receiverId = :userId order by n.createdDate")
    List<Chat> findChatByReceiverId(@Param("userId") UUID userId, @Param("receiverId") UUID receiverId);

    @Query("select n from Chat n " +
            "where n.deleted = false " +
            "and n.contentType = :contentType " +
            "and (n.senderId = :userId and n.receiverId = :receiverId " +
            "     or n.senderId = :receiverId and n.receiverId = :userId) " +
            "order by n.createdDate")
    List<Chat> findImageChatByReceiverId(@Param("userId") UUID userId, @Param("receiverId") UUID receiverId, @Param("contentType") ContentType contentType);

    @Query("select n from Chat n " +
            "where n.deleted = false " +
            "and n.contentType = :contentType " +
            "and (n.senderId = :userId and n.receiverId = :receiverId " +
            "     or n.senderId = :receiverId and n.receiverId = :userId) " +
            "order by n.createdDate")
    List<Chat> findFileChatByReceiverId(@Param("userId") UUID userId, @Param("receiverId") UUID receiverId, @Param("contentType") ContentType contentType);

    @Query("select n from Chat n where n.deleted = false and n.receiverId = :groupId AND n.type = :type order by n.createdDate")
    List<Chat> findByReceiverIdAndType(@Param("groupId") UUID groupId, @Param("type") MessageType type);

    @Query("select n from Chat n where n.deleted = false and n.receiverId = :groupId AND n.type = :type and n.contentType = :contentType order by n.createdDate")
    List<Chat> findImageHistoryChatByGroupId(@Param("groupId") UUID groupId, @Param("type") MessageType type, @Param("contentType") ContentType contentType);

    @Query("SELECT c FROM Chat c WHERE c.deleted = false and ((c.senderId = :userId OR c.receiverId = :userId) AND c.type = :type) and c.deleted = false ")
    List<Chat> findBySenderIdOrReceiverIdAndType(
            @Param("userId") UUID userId,
            @Param("type") MessageType type
    );

    @Query("SELECT c FROM Chat c WHERE c.deleted = false and ((c.senderId = :senderId AND c.receiverId = :receiverId) " +
            "OR (c.senderId = :receiverId AND c.receiverId = :senderId)) AND c.type = :type ORDER BY c.createdDate DESC LIMIT 1")
    Optional<Chat> findTopBySenderIdAndReceiverIdOrSenderIdAndReceiverIdAndTypeOrderByCreatedDateDesc(
            @Param("senderId") UUID senderId,
            @Param("receiverId") UUID receiverId,
            @Param("type") MessageType type
    );

    @Query("SELECT c FROM Chat c WHERE c.receiverId = :groupId AND c.type = :type AND c.deleted = false ORDER BY c.createdDate DESC LIMIT 1")
    Optional<Chat> findTopByReceiverIdAndTypeOrderByCreatedDateDesc(
            @Param("groupId") UUID groupId,
            @Param("type") MessageType type
    );

    List<Chat> findAll(Specification<Chat> spec, Sort sortByCreatedDateDesc);

    void deleteByIdAndSenderId(UUID id, UUID senderId);

    @Query("select c from Chat c where c.senderId = :senderId and c.id = :id")
    Optional<Chat> findBySenderIdAndId(@Param("senderId") UUID senderId,@Param("id") UUID id);
}

