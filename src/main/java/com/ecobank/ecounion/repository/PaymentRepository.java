package com.ecobank.ecounion.repository;

import com.ecobank.ecounion.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Custom query methods can be added here if needed

    List<Payment> findByProjectId(Long projectId);  // Find payments by project ID
    List<Payment> findByRecipientId(Long recipientId);  // Find payments by recipient ID
    List<Payment> findByAdminId(Long adminId);  // Find payments by admin ID
}
