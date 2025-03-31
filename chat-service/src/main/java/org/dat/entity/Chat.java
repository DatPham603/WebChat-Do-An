package org.dat.entity;

import jakarta.persistence.*;
import lombok.*;
import org.dat.enums.MessegeType;
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
public class Chat extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    @Enumerated(EnumType.STRING)
    @Column(name = "message_type")
    private MessegeType type;
    @Column(name = "content")
    private String content;
    @Column(name = "sender_id")
    private UUID senderId;
    @Column(name = "sender")
    private String senderName;
    @Column(name = "receiver_id")
    private UUID receiverId;
}
