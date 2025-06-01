package org.dat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFriendDTO {
    private UUID id;
    private String userName;
    private String email;
    private String address;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String avatar;
    private Boolean isConfirmed;
}
