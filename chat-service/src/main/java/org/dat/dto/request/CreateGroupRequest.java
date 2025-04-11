package org.dat.dto.request;


import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateGroupRequest {
    private String name;
    private String ownerId;
    private List<UUID> memberIds;
}