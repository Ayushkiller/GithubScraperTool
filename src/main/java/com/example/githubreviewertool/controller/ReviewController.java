package com.example.githubreviewertool.controller;

import com.example.githubreviewertool.model.AnalysisResult;
import com.example.githubreviewertool.service.GitHubService;
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

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/analyze")
    public String analyzeProfile(@RequestParam String username, Model model) {
        try {
            log.info("Analyzing profile for username: {}", username);
            AnalysisResult result = gitHubService.analyzeProfile(username);
            model.addAttribute("result", result);
            log.info("Analysis completed successfully");
            return "result";
        } catch (Exception e) {
            log.error("Error analyzing profile", e);
            model.addAttribute("error", "An error occurred during analysis");
            return "error";
        }
    }

    @ExceptionHandler(Exception.class)
    public String handleError(Model model) {
        model.addAttribute("error", "An unexpected error occurred");
        return "error";
    }
}