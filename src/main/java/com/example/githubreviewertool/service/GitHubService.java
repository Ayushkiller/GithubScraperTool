package com.example.githubreviewertool.service;

import com.example.githubreviewertool.model.AnalysisResult;
import com.example.githubreviewertool.model.Repository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GitHubService {

    @Value("${github.token}")
    private String githubToken;

    @Autowired
    private ComplexityAnalyzer complexityAnalyzer;

    public AnalysisResult analyzeProfile(String username) {
        try {
            GitHub github = new GitHubBuilder().withOAuthToken(githubToken).build();
            List<Repository> repositories = github.getUser(username).listRepositories().toList().stream()
                    .map(repo -> new Repository(repo.getName(), repo.getDescription(), repo.getLanguage()))
                    .collect(Collectors.toList());

            Repository mostComplex = complexityAnalyzer.findMostComplexRepository(repositories);
            return new AnalysisResult(username, repositories, mostComplex);
        } catch (IOException e) {
            throw new RuntimeException("Error analyzing GitHub profile", e);
        }
    }
}