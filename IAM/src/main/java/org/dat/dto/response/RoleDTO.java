package org.dat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {
    private String code;
    private String description;
    private String name;
    private Boolean isAdmin;
    private List<PermissionDTO> permissions;
}
