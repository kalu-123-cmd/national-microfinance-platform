package com.microfinance.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConfigRequest {
    @NotBlank private String configKey;
    @NotBlank private String configValue;
    private String description;
    private String category;
    private String dataType;
}
