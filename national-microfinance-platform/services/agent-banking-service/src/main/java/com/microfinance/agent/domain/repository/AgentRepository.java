package com.microfinance.agent.domain.repository;

import com.microfinance.agent.domain.model.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgentRepository extends JpaRepository<Agent, String> {
    Optional<Agent> findByUserId(String userId);
    Optional<Agent> findByBusinessId(String businessId);
    List<Agent> findByStatus(String status);
    List<Agent> findByRegion(String region);
}