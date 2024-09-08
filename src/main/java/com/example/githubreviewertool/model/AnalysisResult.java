package com.example.githubreviewertool.model;

import java.util.List;

public class AnalysisResult {
    private final String username;
    private final List<Repository> repositories;
    private final Repository mostComplexRepository;
    private final double complexityScore;

    public AnalysisResult(String username, List<Repository> repositories, Repository mostComplexRepository, double complexityScore) {
        this.username = username;
        this.repositories = repositories;
        this.mostComplexRepository = mostComplexRepository;
        this.complexityScore = complexityScore;
    }

    public String getUsername() { return username; }
    public List<Repository> getRepositories() { return repositories; }
    public Repository getMostComplexRepository() { return mostComplexRepository; }
    public double getComplexityScore() { return complexityScore; }
}