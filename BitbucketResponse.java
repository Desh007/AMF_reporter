// BitbucketResponse.java
package com.example.report.service;

import lombok.Data;

import java.util.List;

@Data
public class BitbucketResponse {
    private List<PullRequestData> values;
    private int page; // Current page number
    private int pagelen; // Number of items per page
    private int size; // Total number of items
    private boolean isLastPage; // Optional: track if this is the last page
}
