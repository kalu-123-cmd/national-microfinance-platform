package com.microfinance.cooperative.domain.repository;

import com.microfinance.cooperative.domain.model.Cooperative;
import com.microfinance.cooperative.domain.model.CooperativeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CooperativeRepository extends JpaRepository<Cooperative, String> {
    Optional<Cooperative> findByRegistrationNumber(String registrationNumber);
    List<Cooperative> findByAdminUserId(String adminUserId);
    List<Cooperative> findByStatus(CooperativeStatus status);
    boolean existsByRegistrationNumber(String registrationNumber);
}
