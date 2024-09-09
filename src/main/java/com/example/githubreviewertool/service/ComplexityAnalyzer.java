package com.example.githubreviewertool.service;

import com.example.githubreviewertool.model.Repository;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class ComplexityAnalyzer {

    public Repository findMostComplexRepository(List<Repository> repositories) {
        return repositories.stream()
                .max(Comparator.comparingInt(this::calculateComplexityScore))
                .orElse(null);
    }

    public int calculateComplexityScore(Repository repo) {
        int score = 0;
    
        // Lines of Code (LOC)
        score += repo.getLinesOfCode() / 100;
    
        // Number of files and folder depth
        score += repo.getFileCount() / 10;
        score += repo.getFolderDepth() * 5;
    
        // Commit history
        score += repo.getCommitCount() / 50;
        score += repo.getContributorCount() * 2;
    
        // Language weightage
        score += getLanguageScore(repo.getLanguage());
    
        // Framework complexity
        score += getFrameworkComplexityScore(repo.getFrameworks());
    
        // Cyclomatic complexity
        score += repo.getCyclomaticComplexity() * 10;
    
        return score;
    }
    

    private int getLanguageScore(String language) {
        if (language == null || language.isEmpty()) {
            return 0;  // Return 0 if language is null or empty
        }
        switch (language.toLowerCase()) {
            case "java":
                return 10;
            case "python":
                return 8;
            case "javascript":
                return 7;
            case "c++":
                return 12;  // Consider C++ to be more complex
            case "rust":
                return 15;  // Rust can be considered highly complex
            case "go":
                return 9;
            case "php":
                return 6;
            case "ruby":
                return 8;
            case "swift":
                return 11;
            case "kotlin":
                return 10;
            case "typescript":
                return 8;
            case "bash":
                return 5;
            default:
                return 5;  // Default weight for unknown languages
        }
    }

    private int getFrameworkComplexityScore(List<String> frameworks) {
        if (frameworks == null || frameworks.isEmpty()) {
            return 0;  // Return 0 if frameworks is null or empty
        }
        
        int score = 0;
        for (String framework : frameworks) {
            switch (framework.toLowerCase()) {
                case "spring":
                    score += 10;
                    break;
                case "django":
                    score += 8;
                    break;
                case "react":
                    score += 7;
                    break;
                case "angular":
                    score += 9;
                    break;
                default:
                    score += 5;  // Default score for unknown frameworks
            }
        }
        return score;
    }
    
}
