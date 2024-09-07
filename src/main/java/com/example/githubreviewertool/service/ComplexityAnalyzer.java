package com.example.githubreviewertool.service;

import com.example.githubreviewertool.model.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComplexityAnalyzer {

    public Repository findMostComplexRepository(List<Repository> repositories) {
        // TODO: Implement complexity analysis logic
        // For now, we'll just return the first repository
        return repositories.isEmpty() ? null : repositories.get(0);
    }
}