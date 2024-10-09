#!/bin/bash

# Define your variables
BITBUCKET_URL="https://api.bitbucket.org/2.0"
REPO_OWNER="your_repo_owner"
REPO_NAME="your_repo_name"
TARGET_BRANCH="release_branch" # Replace with your actual release branch name
BITBUCKET_USERNAME="your_username" # Bitbucket username
BITBUCKET_APP_PASSWORD="your_app_password" # App password for Bitbucket

# Check for open automerge failure pull requests
RESPONSE=$(curl -s -u "$BITBUCKET_USERNAME:$BITBUCKET_APP_PASSWORD" "$BITBUCKET_URL/repositories/$REPO_OWNER/$REPO_NAME/pullrequests?q=state=\"OPEN\" AND destination.branch.name=\"$TARGET_BRANCH\"")

# Check if the response contains any automerge failure pull requests
if echo "$RESPONSE" | grep -q "automerge failure"; then
    echo "Error: There are open automerge failure pull requests pending to be merged to $TARGET_BRANCH."
    exit 1 # Fail the build
fi

echo "No open automerge failure pull requests found. Proceeding with the build."
