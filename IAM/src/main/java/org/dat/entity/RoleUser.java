package org.dat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles_users")
@EntityListeners(AuditingEntityListener.class)
@Builder
public class RoleUser extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "role_id")
    private UUID roleId;
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "deleted")
    private Boolean deleted;
}

