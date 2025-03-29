package org.dat.mapper;

import org.dat.dto.request.CreateOrUpdateRoleRequest;
import org.dat.dto.response.RoleDTO;
import org.dat.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDTO fromRole(Role role);
    Role fromRoleRequest(CreateOrUpdateRoleRequest request);
}
