package com.microfinance.payment.domain.repository;

import com.microfinance.payment.domain.model.BillPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BillPaymentRepository extends JpaRepository<BillPayment, String> {
    List<BillPayment> findByBillerId(String billerId);
    List<BillPayment> findByBillAccountNumber(String billAccountNumber);
    List<BillPayment> findByPaymentId(String paymentId);
}
