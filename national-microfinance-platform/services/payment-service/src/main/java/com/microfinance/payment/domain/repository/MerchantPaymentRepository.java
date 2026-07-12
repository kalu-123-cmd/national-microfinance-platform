package com.microfinance.payment.domain.repository;

import com.microfinance.payment.domain.model.MerchantPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MerchantPaymentRepository extends JpaRepository<MerchantPayment, String> {
    List<MerchantPayment> findByMerchantId(String merchantId);
    List<MerchantPayment> findByPaymentId(String paymentId);
}
