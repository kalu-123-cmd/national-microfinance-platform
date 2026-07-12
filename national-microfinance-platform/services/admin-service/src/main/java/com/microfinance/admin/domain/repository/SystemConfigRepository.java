package com.microfinance.admin.domain.repository;

import com.microfinance.admin.domain.model.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, String> {
    Optional<SystemConfig> findByConfigKey(String configKey);
    List<SystemConfig> findByCategory(String category);
    boolean existsByConfigKey(String configKey);
}
