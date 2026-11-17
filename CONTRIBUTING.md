# Contributing

Use focused feature branches and pull requests so changes are reviewed and Jenkins validates the merge candidate.

1. Create a feature branch from `main` (for example, `feature/attendance`).
2. Implement a focused change and add tests for new behavior.
3. Run `mvn -B -ntp clean verify` locally.
4. Push your branch and open a pull request against `main`. Complete the pull-request template and include the test result.
5. Address review feedback. Merge only after approval and a successful Jenkins build.

## GitHub and Jenkins setup

Create a Jenkins **Multibranch Pipeline** job for the GitHub repository and configure its GitHub Branch Source to discover branches and origin pull requests. Use the pull-request merge revision so builds validate the proposed changes together with the target branch. Jenkins loads `Jenkinsfile` from each discovered branch or pull request.

Install the GitHub Branch Source and Pipeline plugins. The Jenkins build agent must have JDK 17 or newer and Maven 3.9 or newer on `PATH`. Configure a GitHub webhook to the reachable, secured Jenkins `/github-webhook/` endpoint, or enable periodic multibranch indexing. Require the Jenkins status check and the repository's normal review approvals in GitHub branch protection before merging. The exact status-check name depends on your Jenkins job.

The pipeline runs `mvn -B -ntp clean verify`, publishes the Surefire JUnit XML results, and archives the application JAR. Tests run against H2 and do not require MySQL on the Jenkins agent.

## Local checks

Run the integration suite before requesting review:

```powershell
mvn -B -ntp clean verify
```

The project uses Java 17+, Spring Boot, Maven, MySQL for local application runs, and H2 for automated tests. Never commit credentials, private student data, or production database exports.
