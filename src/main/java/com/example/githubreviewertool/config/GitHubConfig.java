package com.example.githubreviewertool.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import java.io.IOException;

@Configuration
public class GitHubConfig {

    @Value("${github.token}")
    private String githubToken;

    @Bean
    public GitHub gitHub() throws IOException {
        return new GitHubBuilder().withOAuthToken(githubToken).build();

    }
}