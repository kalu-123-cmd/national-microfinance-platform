package com.microfinance.literacy.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class UserStatsResponse {
    private String userId;
    private int totalPoints;
    private long completedCourses;
    private long badgesEarned;
    private List<String> badgeNames;
    private int leaderboardRank;
}
