package org.dat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.dat.enums.Role;

import java.util.UUID;

@Entity
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "group_member")
public class GroupMember {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    @Column(name = "group_id")
    private UUID groupId;
    @Column(name = "userId") // tham chiếu đến user trong auth
    private UUID userId;
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;
}
