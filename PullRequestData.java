// BitbucketResponse.java
package com.example.report.service;

import lombok.Data;

import java.util.List;

@Data
public class BitbucketResponse {
    private List<PullRequestData> values;
}

// PullRequestData.java
package com.example.report.service;

import lombok.Data;

@Data
public class PullRequestData {
    private String id;
    private String title;
    private String description;
    private String createdOn;
    private Author author;
    private List<String> files; // This will need to be adjusted based on the actual API response structure

    @Data
    public static class Author {
        private User user;
    }

    @Data
    public static class User {
        private String displayName;
    }
}
