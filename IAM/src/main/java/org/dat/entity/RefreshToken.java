package org.dat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "refresh_token")
public class RefreshToken extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    @Column(name = "refresh_token", columnDefinition = "TEXT")
    private String refreshToken;
    @Column(name = "expiry_date")
    private Date expiryDate;
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "access_token_id")
    private UUID accessTokenId;
    @Column(name = "access_token_exp")
    private Date accessTokenExp;
}
