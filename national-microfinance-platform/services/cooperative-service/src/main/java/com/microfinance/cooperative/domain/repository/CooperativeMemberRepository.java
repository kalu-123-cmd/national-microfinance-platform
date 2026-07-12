package com.microfinance.cooperative.domain.repository;

import com.microfinance.cooperative.domain.model.CooperativeMember;
import com.microfinance.cooperative.domain.model.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CooperativeMemberRepository extends JpaRepository<CooperativeMember, String> {
    List<CooperativeMember> findByCooperativeId(String cooperativeId);
    Optional<CooperativeMember> findByCooperativeIdAndUserId(String cooperativeId, String userId);
    List<CooperativeMember> findByUserId(String userId);
    List<CooperativeMember> findByCooperativeIdAndStatus(String cooperativeId, MemberStatus status);
    long countByCooperativeIdAndStatus(String cooperativeId, MemberStatus status);
    boolean existsByCooperativeIdAndUserId(String cooperativeId, String userId);
}
