package com.microfinance.offline.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class SyncResponse {
    private int received;
    private int processed;
    private int failed;
    private List<String> failedIds;
    private String message;
}
