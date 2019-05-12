package com.jishnu.sample.repository;

import com.jishnu.sample.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String role);
}
