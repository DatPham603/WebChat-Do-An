package org.dat.mapper;


import org.dat.dto.request.UpdateUserInforRequest;
import org.dat.dto.response.UserDTO;
import org.dat.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO fromUser(User user);

    List<UserDTO> fromListUser(List<User> users);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UpdateUserInforRequest dto, @MappingTarget User entity);
}
