package com.microfinance.literacy.domain.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "user_badges")
public class UserBadge {
    @Id
    private String id;
    private String userId;
    private String badgeName;
    private String badgeDescription;
    private String badgeIcon;
    private String category;
    private int pointsRequired;
    private LocalDateTime earnedAt;
}
