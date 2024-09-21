// ReportController.java
package com.example.report.controller;

import com.example.report.model.PullRequest;
import com.example.report.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class ReportController {

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    private ReportService reportService;

    @GetMapping("/generate-report")
    public String generateReport(@RequestParam String releaseBranch) {
        logger.info("Received request to generate report for release branch: {}", releaseBranch);
        List<PullRequest> pullRequests = reportService.getOpenAutomergeFailures(releaseBranch);
        String filePath = "pull_request_report.csv"; // Adjust the file path as needed
        try {
            reportService.writeReportToCsv(pullRequests, filePath);
            logger.info("Report generated successfully: {}", filePath);
            return "Report generated successfully: " + filePath;
        } catch (IOException e) {
            logger.error("Error generating report: {}", e.getMessage(), e);
            return "Error generating report: " + e.getMessage();
        }
    }
}
