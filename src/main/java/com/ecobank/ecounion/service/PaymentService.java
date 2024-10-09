package com.ecobank.ecounion.service;

import com.ecobank.ecounion.dto.PaymentDTO;
import com.ecobank.ecounion.model.Payment;
import com.ecobank.ecounion.model.Project;
import com.ecobank.ecounion.model.ProjectMembers;
import com.ecobank.ecounion.model.User;
import com.ecobank.ecounion.repository.PaymentRepository;
import com.ecobank.ecounion.repository.ProjectMemberRepository;
import com.ecobank.ecounion.repository.ProjectRepository;
import com.ecobank.ecounion.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class PaymentService {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProjectMemberRepository projectMemberRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Transactional
    public Payment createPayment(String adminEmail, String recipientEmail, String projectName, Double amount, MultipartFile receiptImage) throws IOException {
        // Fetch the project by name
        Project project = projectRepository.findByProjectName(projectName)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with name: " + projectName));

        // Fetch the admin by their email
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found with email: " + adminEmail));

        // Fetch project members to find the recipient
        List<ProjectMembers> projectMembers = projectMemberRepository.findByProject(project);

        // Find the recipient from the project members
        ProjectMembers recipient = projectMembers.stream()
                .filter(member -> member.getEmail().equals(recipientEmail))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found with email: " + recipientEmail + " in project: " + projectName));


        // Check if the amount to be paid is less than the current amount in the project
        if (amount < project.getCurrentAmount()) {
            throw new IllegalArgumentException("The amount to be paid is less than the current amount in the project.");
        }

        // Create and set up the payment
        Payment payment = new Payment();
        payment.setAdmin(admin);
        payment.setRecipient(recipient.getUser()); // Assuming ProjectMembers has a method to get the associated User
        payment.setProject(project);
        payment.setAmount(amount);

        if (receiptImage != null) {
            payment.setReceiptImage(receiptImage);  // Set the receipt image if provided
        }

        payment.setStatus("PENDING");  // Set the initial status as PENDING

        // Save and return the new payment
        return paymentRepository.save(payment);
    }

    public List<PaymentDTO> fetchPaymentsByProject(Long projectId) {
        List<Payment> payments = paymentRepository.findByProjectId(projectId);
        return payments.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<PaymentDTO> fetchAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private PaymentDTO convertToDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setAdminName(payment.getAdmin().getEmail());
        dto.setRecipientName(payment.getRecipient().getEmail());
        dto.setProjectName(payment.getProject().getProjectName());
        dto.setAmount(payment.getAmount());
        dto.setStatus(payment.getStatus());
        dto.setPaymentDate(payment.getPaymentDate());
        byte[] receiptImage = payment.getReceiptImage(); // Assuming this is byte[]
        if (receiptImage != null) {
            String base64ReceiptImage = Base64.getEncoder().encodeToString(receiptImage);
            dto.setReceiptImage(base64ReceiptImage); // Set the Base64 string
        } else {
            dto.setReceiptImage(null); // Handle case where there is no image
        }
        return dto;
    }

    @Transactional
    public Payment approvePayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        if ("APPROVED".equals(payment.getStatus())) {
            throw new IllegalArgumentException("Payment has already been approved.");
        }

        // Calculate the difference between the payment amount and the project's current amount
        Project project = payment.getProject();
        double currentAmount = project.getCurrentAmount();
        double paymentAmount = payment.getAmount(); // Assuming Payment has an amount field
        double remainingAmount = currentAmount - paymentAmount;

        if (remainingAmount < 0) {
            // Return a warning if the payment amount is more than the project's current amount
            System.out.println("Warning: The payment amount is more than the project's current amount.");
            remainingAmount = 0; // Set the project's current amount to 0
        }

        payment.setStatus("APPROVED");

        // Update the project's current amount
        project.setCurrentAmount(remainingAmount);

        // Reset members' dues and apply outstanding dues and interest
        List<ProjectMembers> members = projectMemberRepository.findByProjectId(project.getId());
        for (ProjectMembers member : members) {
            double newDue = project.getMonthlyDues() + member.getOutstandingDues();
            member.setOutstandingDues(newDue);
            projectMemberRepository.save(member);
        }

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment rejectPayment(Long paymentId) {
        // Fetch the payment by ID
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        // Check if the payment has already been approved or rejected
        if ("REJECTED".equals(payment.getStatus())) {
            throw new IllegalArgumentException("Payment has already been rejected.");
        }

        // Set the payment status to REJECTED
        payment.setStatus("REJECTED");

        // You might want to handle additional logic here when a payment is rejected
        // For example, you can notify the admin or recipient, or log this event

        // Save the updated payment
        return paymentRepository.save(payment);
    }



}
