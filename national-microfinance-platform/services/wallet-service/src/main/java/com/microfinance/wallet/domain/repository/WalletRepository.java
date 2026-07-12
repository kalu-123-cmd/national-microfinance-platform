package com.microfinance.wallet.domain.repository;

import com.microfinance.wallet.domain.model.Wallet;
import com.microfinance.wallet.domain.model.WalletStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, String> {
    Optional<Wallet> findByUserId(String userId);
    Optional<Wallet> findByWalletNumber(String walletNumber);
    boolean existsByUserId(String userId);
    boolean existsByWalletNumber(String walletNumber);

    @Modifying
    @Query("UPDATE Wallet w SET w.balance = w.balance + :amount, w.totalCredited = w.totalCredited + :amount WHERE w.id = :walletId AND w.status = 'ACTIVE'")
    int creditWallet(@Param("walletId") String walletId, @Param("amount") BigDecimal amount);

    @Modifying
    @Query("UPDATE Wallet w SET w.balance = w.balance - :amount, w.totalDebited = w.totalDebited + :amount WHERE w.id = :walletId AND w.status = 'ACTIVE' AND w.balance - w.reservedBalance >= :amount")
    int debitWallet(@Param("walletId") String walletId, @Param("amount") BigDecimal amount);

    @Modifying
    @Query("UPDATE Wallet w SET w.status = :status WHERE w.id = :walletId")
    void updateStatus(@Param("walletId") String walletId, @Param("status") WalletStatus status);
}