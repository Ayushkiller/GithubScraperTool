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
import org.eclipse.jgit.api.errors.GitAPIException;

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
    private static final long MAX_REPO_SIZE_MB = 50;  // Maximum repository size to process

    @Autowired
    private GitHub github;

    /**
     * Analyzes a GitHub user's profile and returns the analysis result.
     *
     * This function fetches the user's public repositories, filters out large repositories,
     * and analyzes each repository. The analysis result includes the username and a list
     * of analyzed repositories.
     *
     * @param username the GitHub username to analyze
     * @return the analysis result containing the username and a list of analyzed repositories
     * @throws IOException if an I/O error occurs during the analysis
     */
    public AnalysisResult analyzeProfile(String username) throws IOException {
        logger.info("Analyzing GitHub profile for user: {}", username);
        GHUser user = github.getUser(username);
        List<GHRepository> repos = user.listRepositories().toList();

        List<Repository> analyzedRepos = new ArrayList<>();

        for (GHRepository repo : repos) {
            long repoSizeMB = repo.getSize() / 1024;  // Size in MB
            if (repoSizeMB > MAX_REPO_SIZE_MB) {
                logger.info("Skipping repository '{}' due to size ({} MB)", repo.getName(), repoSizeMB);
                continue;  // Skip the repository if it is too large
            }
            logger.info("Analyzing repository: '{}', size: {} MB", repo.getName(), repoSizeMB);
            Repository analyzedRepo = analyzeRepository(repo);
            analyzedRepos.add(analyzedRepo);
        }

        return new AnalysisResult(username, analyzedRepos);
    }

    /**
     * Analyzes a GitHub repository and returns the analysis result.
     *
     * This function clones the repository, counts the lines of code, files, and folder depth,
     * and then deletes the cloned repository after processing.
     *
     * @param repo the GitHub repository to analyze
     * @return the analysis result containing the repository details
     */
    private Repository analyzeRepository(GHRepository repo) throws IOException {
        // Create a temporary directory to clone the repository into
        String cloneDir = "/tmp/github_repos/" + repo.getName();
        logger.info("Cloning repository: '{}' into {}", repo.getName(), cloneDir);
        cloneRepository(repo.getHttpTransportUrl(), cloneDir);

        // Count the lines of code in the repository
        int linesOfCode = countLinesOfCode(cloneDir);
        // Count the number of files in the repository
        int fileCount = countFiles(cloneDir);
        // Calculate the folder depth of the repository
        int folderDepth = calculateFolderDepth(cloneDir);

        logger.info("Repository '{}' analyzed: {} files, {} lines of code, folder depth: {}",
                repo.getName(), fileCount, linesOfCode, folderDepth);

        // Delete the cloned repository after processing
        deleteDirectory(new File(cloneDir));

        // Create a new Repository object with the analyzed details
        return new Repository(
            repo.getName(),
            repo.getDescription(),
            repo.getLanguage(),
            repo.getWatchersCount(),
            repo.getForksCount(),
            repo.getOpenIssueCount(),
            repo.getCreatedAt(),
            repo.getUpdatedAt(),
            repo.listCommits().toList().size(),
            repo.listContributors().toList().size(),
            fileCount,
            linesOfCode,
            folderDepth,
            0,  
            repo.getWatchersCount()  
        );
    }

    private void cloneRepository(String repoUrl, String cloneDir) throws IOException {
        File dir = new File(cloneDir);
        if (dir.exists()) {
            deleteDirectory(dir);
        }
        try {
            logger.info("Starting to clone repository from URL: {}", repoUrl);
            org.eclipse.jgit.api.Git.cloneRepository()
                    .setURI(repoUrl)
                    .setDirectory(dir)
                    .call();
            logger.info("Repository successfully cloned to: {}", cloneDir);
        } catch (GitAPIException e) {
            logger.error("Failed to clone repository from URL: {}", repoUrl, e);
            throw new IOException("Failed to clone repository", e);
        }
    }

    private int countLinesOfCode(String directoryPath) throws IOException {
        logger.info("Counting lines of code in directory: {}", directoryPath);
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
        logger.info("Counting files in directory: {}", directoryPath);
        try (Stream<Path> walk = Files.walk(Paths.get(directoryPath))) {
            return (int) walk.filter(Files::isRegularFile).count();
        }
    }

    private int calculateFolderDepth(String directoryPath) throws IOException {
        if (directoryPath == null || directoryPath.isEmpty()) {
            return 0;
        }
        logger.info("Calculating folder depth in directory: {}", directoryPath);

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
        logger.info("Deleting directory: {}", directoryToBeDeleted.getAbsolutePath());
        directoryToBeDeleted.delete();
    }
}
