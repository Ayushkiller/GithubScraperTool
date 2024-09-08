package com.example.githubreviewertool.controller;

import com.example.githubreviewertool.model.AnalysisResult;
import com.example.githubreviewertool.model.Repository;
import com.example.githubreviewertool.service.GitHubService;
import com.example.githubreviewertool.service.ComplexityAnalyzer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
public class ReviewController {

    @Autowired
    private GitHubService gitHubService;

    @Autowired
    private ComplexityAnalyzer complexityAnalyzer;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/analyze")
    public String analyzeProfile(@RequestParam String username, Model model) {
        try {
            log.info("Analyzing profile for username: {}", username);
            AnalysisResult result = gitHubService.analyzeProfile(username);
            
            // Find the most complex repository
            Repository mostComplex = complexityAnalyzer.findMostComplexRepository(result.getRepositories());
            result.setMostComplexRepository(mostComplex);
            
            // Calculate overall complexity score
            double overallComplexity = result.getRepositories().stream()
                .mapToInt(repo -> complexityAnalyzer.calculateComplexityScore(repo))
                .average()
                .orElse(0.0);
            result.setComplexityScore(overallComplexity);
            
            model.addAttribute("result", result);
            log.info("Analysis completed successfully");
            return "result";
        } catch (Exception e) {
            log.error("Error analyzing profile", e);
            model.addAttribute("error", "An error occurred during analysis: " + e.getMessage());
            return "error";
        }
    }

    @ExceptionHandler(Exception.class)
    public String handleError(Exception e, Model model) {
        log.error("Unexpected error", e);
        model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
        return "error";
    }
}