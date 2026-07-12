package com.microfinance.cooperative.dto;

import com.microfinance.cooperative.domain.model.CooperativeType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateCooperativeRequest {
    @NotBlank private String name;
    @NotBlank private String registrationNumber;
    @NotNull private CooperativeType type;
    @NotNull @DecimalMin("10.00") private BigDecimal monthlyContribution;
    @Min(2) private int maxMembers;
    private String location;
    private String phone;
}
