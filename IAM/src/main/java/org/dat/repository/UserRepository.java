package org.dat.repository;

import org.dat.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    Optional<User> findByUserName(String userName);

    Optional<User> findById(UUID userId);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

//    @Query(value = "SELECT * FROM users u " +
//            "WHERE unaccent(u.username || ' ' || u.email || ' ' || u.phone_number || ' ' || u.address) " +
//            "ILIKE unaccent(CONCAT('%', :keyword, '%'))", nativeQuery = true)
//    Page<User> findByKeyWord(@Param("keyword") String keyword, Pageable pageable);
}
