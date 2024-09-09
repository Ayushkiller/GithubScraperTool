package com.example.githubreviewertool.model;

import java.util.List;

public class Repository {
    private String name;
    private String description;
    private String language;
    private int stars;
    private int forksCount;
    private int openIssuesCount;
    private java.util.Date createdAt;
    private java.util.Date updatedAt;
    private int commitCount;
    private int contributorCount;
    private int fileCount;
    private int linesOfCode;
    private int cyclomaticComplexity;
    private int folderDepth;
    private int watchers;
    private List<String> frameworks;

    public Repository(String name, String description, String language, int stars, int forksCount, int openIssuesCount,
            java.util.Date date, java.util.Date date2, int commitCount, int contributorCount, int fileCount,
            int linesOfCode, int folderDepth, int cyclomaticComplexity, int watchers) {
        this.name = name;
        this.description = description;
        this.language = language;
        this.stars = stars;
        this.forksCount = forksCount;
        this.openIssuesCount = openIssuesCount;
        this.createdAt = date;
        this.updatedAt = date2;
        this.commitCount = commitCount;
        this.contributorCount = contributorCount;
        this.fileCount = fileCount;
        this.linesOfCode = linesOfCode;
        this.folderDepth = folderDepth;
        this.cyclomaticComplexity = cyclomaticComplexity;
        this.watchers = watchers;
    }

    // Getters and Setters for the new fields
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public java.util.Date getCreatedAt() {
        return createdAt;
    }

    public java.util.Date getUpdatedAt() {
        return updatedAt;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getOpenIssueCount() {
        return openIssuesCount;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public int getForksCount() {
        return forksCount;
    }

    public void setForks(int forksCount) {
        this.forksCount = forksCount;
    }

    public int getWatchers() {
        return watchers;
    }

    public void setWatchers(int watchers) {
        this.watchers = watchers;
    }

    public int getCommitCount() {
        return commitCount;
    }

    public void setCommitCount(int commitCount) {
        this.commitCount = commitCount;
    }

    public int getContributorCount() {
        return contributorCount;
    }

    public void setContributorCount(int contributorCount) {
        this.contributorCount = contributorCount;
    }

    public int getFileCount() {
        return fileCount;
    }

    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }

    public int getFolderDepth() {
        return folderDepth;
    }

    public void setFolderDepth(int folderDepth) {
        this.folderDepth = folderDepth;
    }

    public int getLinesOfCode() {
        return linesOfCode;
    }

    public void setLinesOfCode(int linesOfCode) {
        this.linesOfCode = linesOfCode;
    }

    public int getCyclomaticComplexity() {
        return cyclomaticComplexity;
    }

    public void setCyclomaticComplexity(int cyclomaticComplexity) {
        this.cyclomaticComplexity = cyclomaticComplexity;
    }

    public List<String> getFrameworks() {
        return frameworks;
    }

    public void setFrameworks(List<String> frameworks) {
        this.frameworks = frameworks;
    }
}
