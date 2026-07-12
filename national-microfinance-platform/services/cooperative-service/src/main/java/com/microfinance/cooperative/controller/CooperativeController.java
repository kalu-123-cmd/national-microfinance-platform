package com.microfinance.cooperative.controller;

import com.microfinance.cooperative.domain.model.*;
import com.microfinance.cooperative.dto.*;
import com.microfinance.cooperative.service.CooperativeService;
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
@RequestMapping("/api/v1/cooperatives")
@RequiredArgsConstructor
public class CooperativeController {

    private final CooperativeService cooperativeService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "cooperative-service"));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Cooperative> createCooperative(
            @Valid @RequestBody CreateCooperativeRequest request,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cooperativeService.createCooperative(
                        request.getName(), request.getRegistrationNumber(),
                        user.getUsername(), request.getType(), request.getMonthlyContribution(),
                        request.getMaxMembers(), request.getLocation(), request.getPhone()));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Cooperative>> getAllCooperatives() {
        return ResponseEntity.ok(cooperativeService.getAllCooperatives());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Cooperative> getCooperative(@PathVariable String id) {
        return ResponseEntity.ok(cooperativeService.getCooperativeById(id));
    }

    @PostMapping("/{id}/members")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('COOP_ADMIN')")
    public ResponseEntity<CooperativeMember> addMember(
            @PathVariable String id,
            @Valid @RequestBody AddMemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cooperativeService.addMember(id, request.getUserId(), request.getRole()));
    }

    @GetMapping("/{id}/members")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CooperativeMember>> getMembers(@PathVariable String id) {
        return ResponseEntity.ok(cooperativeService.getMembers(id));
    }

    @PostMapping("/{id}/contributions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Contribution> recordContribution(
            @PathVariable String id,
            @Valid @RequestBody RecordContributionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cooperativeService.recordContribution(
                        id, request.getUserId(), request.getAmount(), request.getMonth()));
    }

    @GetMapping("/{id}/members/{userId}/contributions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Contribution>> getMemberContributions(
            @PathVariable String id, @PathVariable String userId) {
        return ResponseEntity.ok(cooperativeService.getMemberContributions(id, userId));
    }

    @PostMapping("/{id}/rosca/init")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('COOP_ADMIN')")
    public ResponseEntity<List<RoscaCycle>> initRoscaCycles(@PathVariable String id) {
        return ResponseEntity.ok(cooperativeService.initRoscaCycles(id));
    }

    @GetMapping("/{id}/rosca")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RoscaCycle>> getRoscaCycles(@PathVariable String id) {
        return ResponseEntity.ok(cooperativeService.getRoscaCycles(id));
    }

    @PostMapping("/{id}/loans")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GroupLoan> applyForLoan(
            @PathVariable String id,
            @Valid @RequestBody GroupLoanApplicationRequest request,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cooperativeService.applyForGroupLoan(
                        id, user.getUsername(), request.getAmount(),
                        request.getTenureMonths(), request.getPurpose()));
    }

    @GetMapping("/{id}/loans")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<GroupLoan>> getLoans(@PathVariable String id) {
        return ResponseEntity.ok(cooperativeService.getLoansByCooperative(id));
    }

    @PostMapping("/{id}/loans/{loanId}/approve")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('COOP_ADMIN')")
    public ResponseEntity<GroupLoan> approveLoan(
            @PathVariable String id, @PathVariable String loanId,
            @Valid @RequestBody ApproveLoanRequest request,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(cooperativeService.approveGroupLoan(
                loanId, request.getApprovedAmount(), user.getUsername()));
    }

    @PostMapping("/{id}/loans/{loanId}/disburse")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('COOP_ADMIN')")
    public ResponseEntity<GroupLoan> disburseLoan(
            @PathVariable String id, @PathVariable String loanId) {
        return ResponseEntity.ok(cooperativeService.disburseGroupLoan(loanId));
    }

    @PostMapping("/{id}/loans/{loanId}/repayments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GroupLoanRepayment> makeRepayment(
            @PathVariable String id, @PathVariable String loanId,
            @Valid @RequestBody MakeRepaymentRequest request) {
        return ResponseEntity.ok(cooperativeService.makeRepayment(
                loanId, request.getAmount(), request.getPaymentReference()));
    }

    @GetMapping("/{id}/loans/{loanId}/repayments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<GroupLoanRepayment>> getRepaymentSchedule(
            @PathVariable String id, @PathVariable String loanId) {
        return ResponseEntity.ok(cooperativeService.getRepaymentSchedule(loanId));
    }
}
