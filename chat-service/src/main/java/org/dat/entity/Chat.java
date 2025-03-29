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
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    @Column(name = "message_type")
    private MessegeType type;
    @Column(name = "content")
    private String content;
    @Column(name = "sender")
    private String sender;
}
