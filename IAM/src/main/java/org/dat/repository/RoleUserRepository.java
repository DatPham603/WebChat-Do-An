package org.dat.repository;

import org.dat.entity.RoleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoleUserRepository extends JpaRepository<RoleUser, UUID> {
    RoleUser findByUserId(UUID userId);

    List<RoleUser> findAllByUserId(UUID userId);
}
