package com.microfinance.voice.domain.repository;

import com.microfinance.voice.domain.model.UssdSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UssdSessionRepository extends JpaRepository<UssdSession, String> {
    Optional<UssdSession> findBySessionId(String sessionId);
}
