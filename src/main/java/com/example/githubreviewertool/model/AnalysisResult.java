package com.example.githubreviewertool.model;

import java.util.List;
import lombok.Data;

@Data
public class AnalysisResult {
    private final String username;
    private final List<Repository> repositories;
    private Repository mostComplexRepository;
    private double complexityScore;

    public AnalysisResult(String username, List<Repository> repositories) {
        this.username = username;
        this.repositories = repositories;
        this.mostComplexRepository = null;
        this.complexityScore = 0.0;
    }

    public String getUsername() {
        return username;
    }

    public List<Repository> getRepositories() {
        return repositories;
    }

    public Repository getMostComplexRepository() {
        return mostComplexRepository;
    }

    public double getComplexityScore() {
        return complexityScore;
    }
}