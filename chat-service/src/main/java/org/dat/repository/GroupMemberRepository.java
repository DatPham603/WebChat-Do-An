package org.dat.repository;

import org.dat.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, UUID> {
    boolean existsByGroupIdAndUserId(UUID id, UUID id1);

    List<GroupMember> findByUserId(UUID id);

    long countByGroupId(UUID id);
}
