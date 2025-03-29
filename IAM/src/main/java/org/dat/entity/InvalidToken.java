package org.dat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "Invalid_token")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvalidToken {
    @Id
    private UUID id;
    private Date expiryTime;
    private UUID refreshTokenId;
}
