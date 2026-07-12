-- ============================================================
-- Credit Scoring Service Database Schema
-- V1__init_credit.sql
-- ============================================================

CREATE TABLE IF NOT EXISTS credit_scores (
    id                  VARCHAR(36)     PRIMARY KEY,
    user_id             VARCHAR(36)     NOT NULL UNIQUE,
    score               INTEGER         NOT NULL DEFAULT 300,   -- 300-850 range
    risk_level          VARCHAR(20)     NOT NULL DEFAULT 'UNKNOWN', -- VERY_LOW, LOW, MEDIUM, HIGH, VERY_HIGH
    score_components    TEXT,           -- JSON breakdown of score factors
    loan_history_score  INTEGER         DEFAULT 0,  -- 0-200
    payment_behavior    INTEGER         DEFAULT 0,  -- 0-200
    savings_behavior    INTEGER         DEFAULT 0,  -- 0-150
    account_age_score   INTEGER         DEFAULT 0,  -- 0-150
    transaction_score   INTEGER         DEFAULT 0,  -- 0-150
    computed_at         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    next_update_at      TIMESTAMP WITH TIME ZONE,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS credit_score_history (
    id          VARCHAR(36)     PRIMARY KEY,
    user_id     VARCHAR(36)     NOT NULL,
    score       INTEGER         NOT NULL,
    risk_level  VARCHAR(20)     NOT NULL,
    reason      TEXT,
    computed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_credit_scores_user_id      ON credit_scores(user_id);
CREATE INDEX idx_credit_scores_risk_level   ON credit_scores(risk_level);
CREATE INDEX idx_credit_scores_score        ON credit_scores(score);
CREATE INDEX idx_credit_history_user_id     ON credit_score_history(user_id);
CREATE INDEX idx_credit_history_computed    ON credit_score_history(computed_at DESC);
