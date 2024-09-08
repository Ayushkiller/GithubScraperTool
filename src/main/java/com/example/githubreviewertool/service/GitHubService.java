package com.example.githubreviewertool.service;

import com.example.githubreviewertool.model.AnalysisResult;
import com.example.githubreviewertool.model.Repository;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class GitHubService {

    @Value("${github.token}")
    private String githubToken;

    public AnalysisResult analyzeProfile(String username) throws IOException {
        GitHub github = new GitHubBuilder()
                .withOAuthToken(githubToken)
                .build();
        GHUser user = github.getUser(username);
        List<GHRepository> repos = user.listRepositories().toList();

        List<Repository> analyzedRepos = new ArrayList<>();
        Repository mostComplex = null;
        double maxComplexity = 0;

        for (GHRepository repo : repos) {
            Repository analyzedRepo = analyzeRepository(repo);
            analyzedRepos.add(analyzedRepo);

            double complexity = calculateComplexity(analyzedRepo, repo);
            if (complexity > maxComplexity) {
                maxComplexity = complexity;
                mostComplex = analyzedRepo;
            }
        }

        double complexityScore = calculateComplexity(mostComplex, null);

        return new AnalysisResult(username, analyzedRepos, mostComplex, complexityScore);
    }

    private Repository analyzeRepository(GHRepository repo) throws IOException {
        // Clone the repository locally
        String cloneDir = "/tmp/github_repos/" + repo.getName();
        cloneRepository(repo.getHttpTransportUrl(), cloneDir);

        int commitCount = repo.listCommits().asList().size();
        int contributorCount = repo.listContributors().toList().size();
        int fileCount = countFiles(cloneDir);
        int linesOfCode = countLinesOfCode(cloneDir); // Counts lines of code
        int cyclomaticComplexity = analyzeCyclomaticComplexity(cloneDir); // Analyze cyclomatic complexity

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
            commitCount,
            contributorCount,
            fileCount,
            linesOfCode,
            cyclomaticComplexity
        );
    }

    /**
     * Clone the repository locally for analysis
     */
    private void cloneRepository(String repoUrl, String cloneDir) throws IOException {
        File cloneDirFile = new File(cloneDir);
        if (!cloneDirFile.getParentFile().exists()) {
            cloneDirFile.getParentFile().mkdirs(); // Create parent directories if they do not exist
        }
    
        ProcessBuilder pb = new ProcessBuilder("git", "clone", repoUrl, cloneDir);
        pb.redirectErrorStream(true); // Combine error and output streams
        Process process = pb.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }
    
    /**
     * Count files in the cloned repository
     */
    private int countFiles(String directoryPath) throws IOException {
        return (int) Files.walk(Paths.get(directoryPath)).filter(Files::isRegularFile).count();
    }

    /**
     * Count lines of code in the repository by analyzing relevant source files.
     */
    private int countLinesOfCode(String directoryPath) throws IOException {
        return Files.walk(Paths.get(directoryPath))
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".py") || path.toString().endsWith(".js") || path.toString().endsWith(".java"))
                .mapToInt(this::countLinesInFile)
                .sum();
    }

    /**
     * Count the lines of code in an individual file.
     */
    private int countLinesInFile(java.nio.file.Path filePath) {
        try {
            return (int) Files.lines(filePath).count();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Analyze cyclomatic complexity of the repository using static analysis tools.
     */
    private int analyzeCyclomaticComplexity(String directoryPath) throws IOException {
        int complexity = 0;
        // Run ESLint for JavaScript files
        complexity += runCyclomaticComplexityTool("eslint", "--max-warnings=0", "--ext", ".js", directoryPath);
        
        // Run PyLint for Python files
        complexity += runCyclomaticComplexityTool("pylint", directoryPath);
        
        // Add logic for other languages if necessary
        return complexity;
    }
    
    /**
     * Run a cyclomatic complexity tool (ESLint/PyLint) on the repository and return the calculated complexity.
     */
    private int runCyclomaticComplexityTool(String toolName, String... args) throws IOException {
        // Prepare the command with the tool name and arguments
        List<String> command = new ArrayList<>();
        command.add("npm");
        command.add("run");
        command.add(toolName);
        for (String arg : args) {
            command.add(arg);
        }
        
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true); // Combine error and output streams
        Process process = pb.start();
    
        // Capture and process output
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            int complexity = 0;
            while ((line = reader.readLine()) != null) {
                // Parse the output to extract complexity score
                complexity += parseComplexityFromToolOutput(line);
            }
            return complexity;
        }
    }
    

    private int parseComplexityFromToolOutput(String output) {
        // Example: Extract cyclomatic complexity from ESLint or PyLint output
        // You would need to parse the actual output based on the tool you're using
        return 0; // Placeholder: Replace with actual parsing logic
    }

    /**
     * Delete the cloned repository after analysis.
     */
    private void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }

    private double calculateComplexity(Repository repo, GHRepository ghRepo) {
        double complexityScore = repo.getWatchers() * 0.5
                                + repo.getForks() * 0.3
                                + repo.getFileCount() * 0.2
                                + repo.getCommitCount() * 0.1
                                + repo.getContributorCount() * 0.2
                                + repo.getFileCount() * 0.05
                                + repo.getLinesOfCode() / 1000.0
                                + repo.getCyclomaticComplexity() * 0.1;

        return complexityScore;
    }
}
