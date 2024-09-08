package com.example.githubreviewertool.service;

import com.example.githubreviewertool.model.AnalysisResult;
import com.example.githubreviewertool.model.Repository;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Service
public class GitHubService {
    private static final Logger logger = LoggerFactory.getLogger(GitHubService.class);
    @Autowired
    private GitHub github;

    public AnalysisResult analyzeProfile(String username) throws IOException {
        GHUser user = github.getUser(username);
        List<GHRepository> repos = user.listRepositories().toList();

        List<Repository> analyzedRepos = new ArrayList<>();

        for (GHRepository repo : repos) {
            Repository analyzedRepo = analyzeRepository(repo);
            analyzedRepos.add(analyzedRepo);
        }

        return new AnalysisResult(username, analyzedRepos);
    }

    private Repository analyzeRepository(GHRepository repo) throws IOException {
        String cloneDir = "/tmp/github_repos/" + repo.getName();
        cloneRepository(repo.getHttpTransportUrl(), cloneDir);

        int linesOfCode = countLinesOfCode(cloneDir);
        int fileCount = countFiles(cloneDir);
        int folderDepth = calculateFolderDepth(cloneDir);

        // Delete the cloned repository after processing
        deleteDirectory(new File(cloneDir));

        return new Repository(
                repo.getName(),
                repo.getDescription(),
                repo.getLanguage(),
                repo.getStargazersCount(),
                repo.getForksCount(),
                repo.getOpenIssueCount(),
                repo.getCreatedAt(),
                repo.getUpdatedAt(),
                repo.listCommits().toList().size(),
                repo.listContributors().toList().size(),
                fileCount,
                linesOfCode,
                folderDepth);
    }

    private void cloneRepository(String repoUrl, String cloneDir) throws IOException {
        File dir = new File(cloneDir);
        if (dir.exists()) {
            deleteDirectory(dir);
        }
        try {
            org.eclipse.jgit.api.Git.cloneRepository()
                    .setURI(repoUrl)
                    .setDirectory(dir)
                    .call();
        } catch (Exception e) {
            throw new IOException("Failed to clone repository", e);
        }
    }

    private int countLinesOfCode(String directoryPath) throws IOException {
        try (Stream<Path> walk = Files.walk(Paths.get(directoryPath))) {
            return walk.filter(Files::isRegularFile)
                    .mapToInt(this::countLinesInFile)
                    .sum();
        }
    }

    private int countLinesInFile(Path filePath) {
        try {
            try (InputStream inputStream = Files.newInputStream(filePath)) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                return (int) reader.lines().count();
            }
        } catch (IOException e) {
            logger.warn("Error reading file: {}", filePath, e);
            return 0;
        }
    }

    private int countFiles(String directoryPath) throws IOException {
        try (Stream<Path> walk = Files.walk(Paths.get(directoryPath))) {
            return (int) walk.filter(Files::isRegularFile).count();
        }
    }

    private int calculateFolderDepth(String directoryPath) throws IOException {
        if (directoryPath == null || directoryPath.isEmpty()) {
            return 0;
        }

        Path path = Paths.get(directoryPath);
        try (Stream<Path> walk = Files.walk(path)) {
            // Escape special characters in the separator
            String separatorEscaped = Pattern.quote(File.separator);

            return walk.filter(Files::isDirectory)
                    .flatMap(p -> {
                        try {
                            return Stream.of(p.toRealPath());
                        } catch (IOException e) {
                            logger.warn("Error resolving real path: {}", p, e);
                            return Stream.empty();
                        }
                    })
                    .map(Path::toString)
                    .map(s -> s.split(separatorEscaped)) // Use the escaped separator
                    .map(arr -> arr.length)
                    .max(Integer::compare)
                    .orElseThrow(() -> new IOException("Unable to determine real path"));
        }
    }

    private void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }
}