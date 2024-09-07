package com.example.githubreviewertool.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AnalysisResult {
    private String username;
    private List<Repository> repositories;
    private Repository mostComplexRepository;
}