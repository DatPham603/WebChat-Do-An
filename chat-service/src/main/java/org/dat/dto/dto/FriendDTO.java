package org.dat.dto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendDTO {
    private UUID userId;
    private UUID friendId;
    private String friendName;
    private Boolean confirmed;
}