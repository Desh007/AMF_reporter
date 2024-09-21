// ReportService.java
package com.example.report.service;

import com.example.report.model.PullRequest;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    @Value("${bitbucket.api.url}")
    private String bitbucketApiUrl;

    @Value("${bitbucket.username}")
    private String bitbucketUsername;

    @Value("${bitbucket.password}")
    private String bitbucketPassword;

    public List<PullRequest> getOpenAutomergeFailures(String releaseBranch) {
        RestTemplate restTemplate = new RestTemplate();
        String urlTemplate = String.format("%s/repositories/{workspace}/{repo_slug}/pullrequests?state=OPEN&pagelen=100&page={page}", bitbucketApiUrl);
        
        List<PullRequest> pullRequests = new ArrayList<>();
        int page = 1;
        
        try {
            while (true) {
                logger.info("Fetching page {} of open pull requests for branch: {}", page, releaseBranch);
                BitbucketResponse response = restTemplate.getForObject(urlTemplate, BitbucketResponse.class, "your_workspace", "your_repo_slug", page);
                
                if (response != null && response.getValues() != null && !response.getValues().isEmpty()) {
                    int serialNo = pullRequests.size() + 1; // Maintain correct serial numbers
                    for (PullRequestData prData : response.getValues()) {
                        if (prData.getTitle().contains("automerge failure")) {
                            PullRequest pr = new PullRequest(
                                    serialNo++,
                                    prData.getId(),
                                    String.join(", ", prData.getFiles()), // Extract affected files
                                    prData.getAuthor().getUser().getDisplayName(),
                                    prData.getCreatedOn(),
                                    prData.getDescription() // Comments/description
                            );
                            pullRequests.add(pr);
                            logger.debug("Added PR: {}", pr); // Log added PR details
                        }
                    }
                    page++;
                } else {
                    logger.info("No more pull requests found or reached the last page.");
                    break;
                }
            }
        } catch (HttpClientErrorException e) {
            logger.error("Error fetching pull requests: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("Unexpected error occurred: {}", e.getMessage(), e);
        }
        
        return pullRequests;
    }

    public void writeReportToCsv(List<PullRequest> pullRequests, String filePath) throws IOException {
        logger.info("Writing report to CSV at: {}", filePath);
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            String[] header = {"Serial No", "Pull Request ID", "Affected Files", "Owner", "Date Opened", "Comments"};
            writer.writeNext(header);

            for (PullRequest pr : pullRequests) {
                String[] record = {
                        String.valueOf(pr.getSerialNo()),
                        pr.getPullRequestId(),
                        pr.getAffectedFiles(),
                        pr.getOwner(),
                        pr.getDateOpened(),
                        pr.getComments()
                };
                writer.writeNext(record);
                logger.debug("Written PR to CSV: {}", pr); // Log written PR details
            }
        }
    }
}
