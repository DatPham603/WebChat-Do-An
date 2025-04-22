package org.dat.dto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private UUID id;
    private String userName;
    private String email;
    private String address;
    private List<String> roleName;
    private LocalDate dateOfBirth;
    private List<String> perDescription;
    private String avatar;
}
