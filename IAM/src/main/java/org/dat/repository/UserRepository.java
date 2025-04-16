package org.dat.repository;

import org.dat.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    Optional<User> findByUserName(String userName);

    Optional<User> findById(UUID userId);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Query("select n from User n where n.deleted = false and n.userName like %:userName% ")
    Optional<User> findUserByUserName(@Param("userName") String userName);

//    @Query(value = "SELECT * FROM users u " +
//            "WHERE unaccent(u.username || ' ' || u.email || ' ' || u.phone_number || ' ' || u.address) " +
//            "ILIKE unaccent(CONCAT('%', :keyword, '%'))", nativeQuery = true)
//    Page<User> findByKeyWord(@Param("keyword") String keyword, Pageable pageable);

    @Query("select n from User n where n.deleted = false and n.id in :id")
    List<User> findUserByUserId(@Param("id") List<UUID> id);
}
