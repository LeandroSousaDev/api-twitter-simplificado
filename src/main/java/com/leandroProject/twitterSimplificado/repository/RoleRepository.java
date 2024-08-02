package com.leandroProject.twitterSimplificado.repository;

import com.leandroProject.twitterSimplificado.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
