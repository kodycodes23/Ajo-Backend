package com.ecobank.ecounion.repository;
import com.ecobank.ecounion.model.PaymentSchedule;
import com.ecobank.ecounion.model.Project;
import com.ecobank.ecounion.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentScheduleRepository  extends JpaRepository<PaymentSchedule, Long> {

    // Custom query methods can go here if needed, e.g.:
    List<PaymentSchedule> findByProject(Project project);
    List<PaymentSchedule> findByProjectId(Long projectId);
    List<PaymentSchedule> findByProject_ProjectName(String projectName);

    List<PaymentSchedule> findByMember(User member);
}
