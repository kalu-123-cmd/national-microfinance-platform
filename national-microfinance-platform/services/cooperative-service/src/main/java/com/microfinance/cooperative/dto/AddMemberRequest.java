package com.microfinance.cooperative.dto;

import com.microfinance.cooperative.domain.model.MemberRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddMemberRequest {
    @NotBlank private String userId;
    private MemberRole role = MemberRole.MEMBER;
}
