# GitHub Reviewer Tool

## Overview

The GitHub Reviewer Tool is a Java-based application designed to analyze GitHub user profiles and identify the most technically complex repository. This tool provides insights into a user's public repositories, including repository details, language usage, and complexity metrics.

## Features

- Fetch and analyze all public repositories for a given GitHub username
- Calculate complexity scores based on various metrics (to be implemented)
- Identify the most technically complex repository
- Present analysis results through a web interface

## Prerequisites

- Java 11 or higher
- Gradle 6.8 or higher (included with the project as a wrapper)
- A GitHub account and personal access token

## Installation

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/github-reviewer-tool.git
   cd github-reviewer-tool
   ```

2. Set up your GitHub token:
   - Create a `application.properties` file in `src/main/resources/` if it doesn't exist
   - Add your GitHub token to the file:
     ```
     github.token=your_github_token_here
     ```

3. Build the project:
   ```
   ./gradlew build
   ```

## Usage

1. Start the application:
   ```
   ./gradlew bootRun
   ```

2. Open a web browser and navigate to `http://localhost:8080`

3. Enter a GitHub username in the provided form and click "Analyze"

4. View the analysis results, including repository details and the most complex repository

## Project Structure

```
project-root/
├── src/
│   ├── main/
│   │   ├── java/com/example/githubreviewertool/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── model/
│   │   │   └── config/
│   │   └── resources/
│   │       └── templates/
│   └── test/
├── build.gradle
└── settings.gradle
```

## Key Components

- `ReviewController`: Handles HTTP requests and manages the web interface
- `GitHubService`: Interacts with the GitHub API to fetch repository data
- `ComplexityAnalyzer`: Analyzes repositories and calculates complexity scores
- `Repository` and `AnalysisResult`: Model classes for data representation

## Customization

To modify the complexity analysis logic, edit the `ComplexityAnalyzer` class in the `service` package. You can implement your own algorithms or integrate third-party tools for more advanced analysis.

## Contributing

Contributions to the GitHub Reviewer Tool are welcome! Please follow these steps:

1. Fork the repository
2. Create a new branch: `git checkout -b feature-branch-name`
3. Make your changes and commit them: `git commit -m 'Add some feature'`
4. Push to the branch: `git push origin feature-branch-name`
5. Submit a pull request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot) for the web application framework
- [GitHub API for Java](https://github-api.kohsuke.org/) for GitHub integration
- [Gradle](https://gradle.org/) for build automation

## Contact

For questions or feedback, please open an issue in the GitHub repository or contact the maintainer at [malikayush999@gmail.com](malikayush999@gmail.com).