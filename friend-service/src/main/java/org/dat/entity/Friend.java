package org.dat.entity;

import jakarta.persistence.*;
import lombok.*;
import org.dat.enums.FriendRequestDirection;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "friend")
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Friend extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    @Column(name = "userId")
    private UUID userId;
    @Column(name = "friendId")
    private UUID friendId;
    @Column(name = "userName")
    private String userName;
    @Column(name = "friendName")
    private String friendName;
    @Column(name = "email")
    private String email;
    @Column(name = "friendEmail")
    private String friendEmail;
    @Column(name = "confirmed")
    private boolean confirmed;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "direction")
    @Enumerated(EnumType.STRING)
    private FriendRequestDirection direction;
}
