package com.microfinance.agent.service;

import com.microfinance.agent.domain.model.*;
import com.microfinance.agent.domain.repository.*;
import com.microfinance.agent.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AgentServiceImpl implements AgentService {

    private final AgentRepository agentRepository;
    private final CashTransactionRepository transactionRepository;

    @Value("${agent.commission.cash-in-rate:0.005}")
    private BigDecimal cashInCommissionRate;

    @Value("${agent.commission.cash-out-rate:0.010}")
    private BigDecimal cashOutCommissionRate;

    @Override
    @Transactional
    public AgentResponse registerAgent(AgentRegistrationRequest request) {
        if (agentRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new IllegalArgumentException("Agent already registered for user: " + request.getUserId());
        }
        if (agentRepository.findByBusinessId(request.getBusinessId()).isPresent()) {
            throw new IllegalArgumentException("Business ID already registered: " + request.getBusinessId());
        }

        Agent agent = Agent.builder()
                .id(UUID.randomUUID().toString())
                .userId(request.getUserId())
                .businessName(request.getBusinessName())
                .businessId(request.getBusinessId())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .region(request.getRegion())
                .woreda(request.getWoreda())
                .kebele(request.getKebele())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .status(AgentStatus.PENDING_APPROVAL.name())
                .dailyLimit(new BigDecimal("50000.00"))
                .monthlyLimit(new BigDecimal("500000.00"))
                .dailyProcessed(BigDecimal.ZERO)
                .monthlyProcessed(BigDecimal.ZERO)
                .balance(BigDecimal.ZERO)
                .build();

        agent = agentRepository.save(agent);
        log.info("Agent registered: {} for user: {}", agent.getBusinessId(), agent.getUserId());
        return toResponse(agent);
    }

    @Override
    @Transactional
    public CashTransactionResponse cashIn(String agentId, CashInRequest request) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("Agent not found: " + agentId));

        if (!AgentStatus.ACTIVE.name().equals(agent.getStatus())) {
            throw new IllegalStateException("Agent is not active");
        }
        
        checkLimits(agent, request.getAmount());

        BigDecimal fee = BigDecimal.ZERO; // Typically free for cash-in for user
        BigDecimal commission = request.getAmount().multiply(cashInCommissionRate).setScale(2, RoundingMode.HALF_UP);

        updateAgentVault(agent, request.getAmount(), commission, true);

        CashTransaction tx = CashTransaction.builder()
                .id(UUID.randomUUID().toString())
                .agentId(agentId)
                .userId(request.getUserId())
                .walletId(request.getWalletId())
                .transactionType(TransactionType.CASH_IN.name())
                .amount(request.getAmount())
                .fee(fee)
                .commission(commission)
                .status("COMPLETED")
                .reference(request.getReference() != null ? request.getReference() : generateReference("CI"))
                .channel(request.getChannel())
                .processedAt(Instant.now())
                .build();

        tx = transactionRepository.save(tx);
        log.info("Cash-in processed: {} ETB for user: {} by agent: {}. Commission: {}", request.getAmount(), request.getUserId(), agentId, commission);
        return toTxResponse(tx);
    }

    @Override
    @Transactional
    public CashTransactionResponse cashOut(String agentId, CashOutRequest request) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("Agent not found: " + agentId));

        if (!AgentStatus.ACTIVE.name().equals(agent.getStatus())) {
            throw new IllegalStateException("Agent is not active");
        }
        
        checkLimits(agent, request.getAmount());
        
        // Ensure agent has enough vault balance to give cash out
        if (agent.getBalance().compareTo(request.getAmount()) < 0) {
             throw new IllegalStateException("Insufficient agent vault balance for cash out");
        }

        BigDecimal fee = request.getAmount().multiply(new BigDecimal("0.02")).setScale(2, RoundingMode.HALF_UP); // 2% fee
        BigDecimal commission = request.getAmount().multiply(cashOutCommissionRate).setScale(2, RoundingMode.HALF_UP);

        updateAgentVault(agent, request.getAmount(), commission, false);

        CashTransaction tx = CashTransaction.builder()
                .id(UUID.randomUUID().toString())
                .agentId(agentId)
                .userId(request.getUserId())
                .walletId(request.getWalletId())
                .transactionType(TransactionType.CASH_OUT.name())
                .amount(request.getAmount())
                .fee(fee)
                .commission(commission)
                .status("COMPLETED")
                .reference(request.getReference() != null ? request.getReference() : generateReference("CO"))
                .channel(request.getChannel())
                .processedAt(Instant.now())
                .build();

        tx = transactionRepository.save(tx);
        log.info("Cash-out processed: {} ETB for user: {} by agent: {}. Commission: {}", request.getAmount(), request.getUserId(), agentId, commission);
        return toTxResponse(tx);
    }
    
    private void checkLimits(Agent agent, BigDecimal amount) {
        if (agent.getDailyProcessed().add(amount).compareTo(agent.getDailyLimit()) > 0) {
            throw new IllegalStateException("Daily transaction limit exceeded");
        }
        if (agent.getMonthlyProcessed().add(amount).compareTo(agent.getMonthlyLimit()) > 0) {
            throw new IllegalStateException("Monthly transaction limit exceeded");
        }
    }
    
    private void updateAgentVault(Agent agent, BigDecimal amount, BigDecimal commission, boolean isCashIn) {
        agent.setDailyProcessed(agent.getDailyProcessed().add(amount));
        agent.setMonthlyProcessed(agent.getMonthlyProcessed().add(amount));
        
        if (isCashIn) {
            // Agent receives digital float, gives physical cash
            agent.setBalance(agent.getBalance().add(amount).add(commission));
        } else {
            // Agent gives digital float, receives physical cash
            agent.setBalance(agent.getBalance().subtract(amount).add(commission));
        }
        
        agentRepository.save(agent);
    }

    @Override
    public List<CashTransactionResponse> getTransactions(String agentId) {
        return transactionRepository.findByAgentId(agentId).stream()
                .map(this::toTxResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AgentResponse getAgent(String agentId) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("Agent not found: " + agentId));
        return toResponse(agent);
    }

    @Override
    public List<AgentResponse> findNearbyAgents(Double latitude, Double longitude, Double radiusKm) {
        // Note: Full geospatial query would require PostGIS or MongoDB
        // This is a simplified stub returning active agents
        return agentRepository.findByStatus(AgentStatus.ACTIVE.name()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private String generateReference(String prefix) {
        return prefix + "-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private AgentResponse toResponse(Agent agent) {
        return AgentResponse.builder()
                .id(agent.getId())
                .userId(agent.getUserId())
                .businessName(agent.getBusinessName())
                .businessId(agent.getBusinessId())
                .phoneNumber(agent.getPhoneNumber())
                .email(agent.getEmail())
                .region(agent.getRegion())
                .woreda(agent.getWoreda())
                .kebele(agent.getKebele())
                .latitude(agent.getLatitude())
                .longitude(agent.getLongitude())
                .status(agent.getStatus())
                .dailyLimit(agent.getDailyLimit())
                .monthlyLimit(agent.getMonthlyLimit())
                .dailyProcessed(agent.getDailyProcessed())
                .monthlyProcessed(agent.getMonthlyProcessed())
                .balance(agent.getBalance())
                .createdAt(agent.getCreatedAt())
                .build();
    }

    private CashTransactionResponse toTxResponse(CashTransaction tx) {
        return CashTransactionResponse.builder()
                .id(tx.getId())
                .agentId(tx.getAgentId())
                .userId(tx.getUserId())
                .walletId(tx.getWalletId())
                .transactionType(tx.getTransactionType())
                .amount(tx.getAmount())
                .fee(tx.getFee())
                .commission(tx.getCommission())
                .status(tx.getStatus())
                .reference(tx.getReference())
                .channel(tx.getChannel())
                .failureReason(tx.getFailureReason())
                .processedAt(tx.getProcessedAt())
                .build();
    }
}