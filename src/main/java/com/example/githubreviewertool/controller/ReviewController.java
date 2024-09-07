package com.example.githubreviewertool.controller;

import com.example.githubreviewertool.model.AnalysisResult;
import com.example.githubreviewertool.service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReviewController {

    @Autowired
    private GitHubService gitHubService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/analyze")
    public String analyzeProfile(@RequestParam String username, Model model) {
        AnalysisResult result = gitHubService.analyzeProfile(username);
        model.addAttribute("result", result);
        return "result";
    }
}