package org.dat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrUpdateRoleRequest {
    private String code;
    private String description;
    private String name;
    private boolean isAdmin;
}
