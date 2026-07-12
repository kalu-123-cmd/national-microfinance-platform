package com.microfinance.agent.controller;

import com.microfinance.agent.dto.*;
import com.microfinance.agent.service.AgentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/agents")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "agent-banking-service"));
    }

    @PostMapping("/register")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AgentResponse> registerAgent(
            @Valid @RequestBody AgentRegistrationRequest request,
            @AuthenticationPrincipal UserDetails user) {
        // Enforce registering for own userId
        if (!user.getUsername().equals(request.getUserId()) && !user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
             throw new org.springframework.security.access.AccessDeniedException("Can only register agent for own user ID");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(agentService.registerAgent(request));
    }

    @GetMapping("/{agentId}")
    public ResponseEntity<AgentResponse> getAgent(@PathVariable String agentId) {
        return ResponseEntity.ok(agentService.getAgent(agentId));
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<AgentResponse>> getNearbyAgents(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "5.0") Double radiusKm) {
        return ResponseEntity.ok(agentService.findNearbyAgents(latitude, longitude, radiusKm));
    }

    @PostMapping("/{agentId}/cash-in")
    @PreAuthorize("hasRole('AGENT') or hasAuthority('AGENT_ROLE')")
    public ResponseEntity<CashTransactionResponse> cashIn(
            @PathVariable String agentId,
            @Valid @RequestBody CashInRequest request) {
        return ResponseEntity.ok(agentService.cashIn(agentId, request));
    }

    @PostMapping("/{agentId}/cash-out")
    @PreAuthorize("hasRole('AGENT') or hasAuthority('AGENT_ROLE')")
    public ResponseEntity<CashTransactionResponse> cashOut(
            @PathVariable String agentId,
            @Valid @RequestBody CashOutRequest request) {
        return ResponseEntity.ok(agentService.cashOut(agentId, request));
    }

    @GetMapping("/{agentId}/transactions")
    @PreAuthorize("hasRole('AGENT') or hasAuthority('AGENT_ROLE')")
    public ResponseEntity<List<CashTransactionResponse>> getTransactions(@PathVariable String agentId) {
        return ResponseEntity.ok(agentService.getTransactions(agentId));
    }
}