package com.microfinance.offline.dto;

import com.microfinance.offline.domain.model.SyncRequest;
import lombok.Data;
import java.util.List;

@Data
public class SyncBatchRequest {
    private String deviceId;
    private String userId;
    private List<SyncRequest> items;
}
