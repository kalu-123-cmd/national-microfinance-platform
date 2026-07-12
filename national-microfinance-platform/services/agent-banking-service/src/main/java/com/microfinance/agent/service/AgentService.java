package com.microfinance.agent.service;

import com.microfinance.agent.dto.*;

import java.util.List;

public interface AgentService {
    AgentResponse registerAgent(AgentRegistrationRequest request);
    CashTransactionResponse cashIn(String agentId, CashInRequest request);
    CashTransactionResponse cashOut(String agentId, CashOutRequest request);
    List<CashTransactionResponse> getTransactions(String agentId);
    AgentResponse getAgent(String agentId);
    List<AgentResponse> findNearbyAgents(Double latitude, Double longitude, Double radiusKm);
}