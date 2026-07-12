package com.microfinance.event;

public final class KafkaTopics {
    private KafkaTopics() {}
    // User
    public static final String USER_REGISTERED         = "user.registered";
    public static final String USER_UPDATED            = "user.updated";
    public static final String USER_SUSPENDED          = "user.suspended";
    public static final String KYC_SUBMITTED           = "kyc.submitted";
    public static final String KYC_APPROVED            = "kyc.approved";
    public static final String KYC_REJECTED            = "kyc.rejected";
    // Auth
    public static final String AUTH_LOGIN_SUCCESS      = "auth.login.success";
    public static final String AUTH_LOGIN_FAILED       = "auth.login.failed";
    public static final String AUTH_PASSWORD_CHANGED   = "auth.password.changed";
    // Wallet
    public static final String WALLET_CREATED          = "wallet.created";
    public static final String WALLET_CREDITED         = "wallet.credited";
    public static final String WALLET_DEBITED          = "wallet.debited";
    public static final String WALLET_FROZEN           = "wallet.frozen";
    // Payment
    public static final String PAYMENT_INITIATED       = "payment.initiated";
    public static final String PAYMENT_COMPLETED       = "payment.completed";
    public static final String PAYMENT_FAILED          = "payment.failed";
    public static final String PAYMENT_REVERSED        = "payment.reversed";
    // Loan
    public static final String LOAN_APPLIED            = "loan.application.submitted";
    public static final String LOAN_APPROVED           = "loan.approved";
    public static final String LOAN_REJECTED           = "loan.rejected";
    public static final String LOAN_DISBURSED          = "loan.disbursed";
    public static final String LOAN_REPAYMENT          = "loan.repayment.received";
    public static final String LOAN_DEFAULTED          = "loan.defaulted";
    public static final String LOAN_COMPLETED          = "loan.completed";
    // Savings
    public static final String SAVINGS_OPENED          = "savings.account.opened";
    public static final String SAVINGS_DEPOSITED       = "savings.deposited";
    public static final String SAVINGS_WITHDRAWN       = "savings.withdrawn";
    public static final String SAVINGS_GOAL_ACHIEVED   = "savings.goal.achieved";
    // Cooperative
    public static final String COOPERATIVE_CREATED     = "cooperative.created";
    public static final String COOPERATIVE_MEMBER      = "cooperative.member.joined";
    public static final String COOPERATIVE_CONTRIB     = "cooperative.contribution.made";
    // Agent
    public static final String AGENT_REGISTERED        = "agent.registered";
    public static final String AGENT_TRANSACTION       = "agent.transaction.completed";
    public static final String AGENT_FLOAT_LOW         = "agent.float.low";
    // Fraud
    public static final String FRAUD_ALERT             = "fraud.alert.raised";
    public static final String TRANSACTION_FLAGGED     = "transaction.flagged";
    // Notification
    public static final String NOTIFY_SMS              = "notification.send.sms";
    public static final String NOTIFY_EMAIL            = "notification.send.email";
    public static final String NOTIFY_PUSH             = "notification.send.push";
    // Audit
    public static final String AUDIT_EVENT             = "audit.event";
    // Credit / AI
    public static final String CREDIT_SCORE_REQUESTED  = "credit.score.requested";
    public static final String CREDIT_SCORE_COMPUTED   = "credit.score.computed";
    public static final String AI_RECOMMENDATION       = "ai.recommendation.ready";
    public static final String FINANCIAL_TWIN_UPDATE   = "financial.twin.update";
    // Offline
    public static final String OFFLINE_TX_QUEUED       = "offline.transaction.queued";
    public static final String OFFLINE_SYNC_DONE       = "offline.sync.completed";
}
