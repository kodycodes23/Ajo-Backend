package com.ecobank.ecounion.controller;

import com.ecobank.ecounion.dto.PaymentDTO;
import com.ecobank.ecounion.model.Payment;
import com.ecobank.ecounion.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    @PostMapping("/create-payment")
    public ResponseEntity<String> createPayment(
            @RequestParam String adminEmail,
            @RequestParam String recipientEmail,
            @RequestParam String projectName,
            @RequestParam Double amount,
            @RequestParam MultipartFile receiptImage) {

        try {
            paymentService.createPayment(adminEmail, recipientEmail, projectName, amount, receiptImage);
            return ResponseEntity.ok("Payment created successfully.");
        } catch (IllegalArgumentException | IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/approve-payment/{paymentId}")
    public ResponseEntity<String> approvePayment(@PathVariable Long paymentId) {
        try {
            paymentService.approvePayment(paymentId);
            return ResponseEntity.ok("Payment approved.");
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/reject/{paymentId}")
    public ResponseEntity<String> rejectPayment(@PathVariable Long paymentId) {
        try {
            Payment rejectedPayment = paymentService.rejectPayment(paymentId);
            return ResponseEntity.ok("Payment rejected successfully: " + rejectedPayment.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error rejecting payment: " + e.getMessage());
        }
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<PaymentDTO>> fetchPaymentsByProject(@PathVariable Long projectId) {
        try {
            List<PaymentDTO> payments = paymentService.fetchPaymentsByProject(projectId);
            return ResponseEntity.ok(payments);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<PaymentDTO>> fetchAllPayments() {
        try {
            List<PaymentDTO> payments = paymentService.fetchAllPayments();
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



}
