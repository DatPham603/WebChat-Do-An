package org.dat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
@EntityListeners(AuditingEntityListener.class)
@Builder
public class Role extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "code")
    private String code;
    @Column(name = "description")
    private String description;
    @Column(name = "name")
    private String name;
    @Column(name = "is_admin")
    private Boolean isAdmin;
}
