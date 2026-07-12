package com.microfinance.admin.domain.repository;

import com.microfinance.admin.domain.model.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, String> {
    Optional<AdminUser> findByUserId(String userId);
    Optional<AdminUser> findByUsername(String username);
    List<AdminUser> findByRole(String role);
    List<AdminUser> findByActiveTrue();
}
