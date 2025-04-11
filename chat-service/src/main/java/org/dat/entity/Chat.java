package org.dat.entity;

import jakarta.persistence.*;
import lombok.*;
import org.dat.enums.ContentType;
import org.dat.enums.MessageType;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "chat")
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Chat extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    @Enumerated(EnumType.STRING)
    @Column(name = "message_type")
    private MessageType type;
    @Column(name = "content")
    private String content;
    @Column(name = "sender_id")
    private UUID senderId;
    @Column(name = "sender")
    private String senderName;
    @Column(name = "receiver_id")
    private UUID receiverId;
    @Column(name = "deleted")
    private Boolean deleted;
    @Column(name = "file_url")
    private String fileUrl;
    @Enumerated(EnumType.STRING)
    @Column(name = "content_type")
    private ContentType contentType;
}
