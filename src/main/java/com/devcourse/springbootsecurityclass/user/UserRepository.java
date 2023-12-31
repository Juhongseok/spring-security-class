package com.devcourse.springbootsecurityclass.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"group", "group.groupPermissions", "group.groupPermissions.permission"})
    Optional<User> findByLoginId(String loginId);

}
