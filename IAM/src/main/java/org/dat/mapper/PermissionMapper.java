package org.dat.mapper;

import org.dat.dto.request.CreateOrUpdatePermissionRequest;
import org.dat.dto.response.PermissionDTO;
import org.dat.entity.Permission;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission fromRequest(CreateOrUpdatePermissionRequest request);
    PermissionDTO fromPermission(Permission permission);
    List<PermissionDTO> fromPermissions(List<Permission> permissions);
}
