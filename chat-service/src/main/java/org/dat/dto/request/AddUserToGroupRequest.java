package org.dat.dto.request;

import lombok.Data;

@Data
public class AddUserToGroupRequest {
    private String ownerId;
    private String email;
}
