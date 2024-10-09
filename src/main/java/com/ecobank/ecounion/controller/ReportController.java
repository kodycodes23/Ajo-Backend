package com.ecobank.ecounion.controller;

import com.ecobank.ecounion.dto.ReportAnalyticsDTO;
import com.ecobank.ecounion.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // Endpoint to get analytics for a specific project
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<ReportAnalyticsDTO>> getProjectAnalytics(@PathVariable Long projectId) {
        try {
            List<ReportAnalyticsDTO> reportData = reportService.getProjectAnalytics(projectId);
            return ResponseEntity.ok(reportData);
        } catch (IllegalArgumentException e) {
            // Handle case where project or data is not found
            return ResponseEntity.badRequest().body(null);
        }
    }
}
