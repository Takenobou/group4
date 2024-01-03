# Running the PasswordManagerApp

This guide will help you run the PasswordManagerApp, which is the main file of the application, and navigate through its features.

## Pre-requisites

- Java JDK 17 or higher
- Gradle

## How to Run

1. **Start the Application**: Run `PasswordManagerApp.java` which is the entry point to the application.
2. **Follow Console Commands**: After running the application, follow the commands displayed in the console to interact with the program.

## Application Commands

- **create**: Begin using the application by creating new credentials.
- **strength**: See the password strength rating for your credentials.
- **sort_strength**: Sort your passwords by their strength rating.
- **delete**: Delete a specific credential.
- **delete_all**: Delete all credentials.
- **last_accessed**: View the most recently viewed credential. This only updates when you use the view command.
- **view**: View the passwords and other details of your credentials.
- **exit**: Exit out of the application.

## Coverage Metric Explanation

In this group project, the coverage report is configured to the test code that I have written compared to the project as a whole. This may not provide an accurate reflection of my individual contribution to the project. It is taking into account paths and files that are not part of my contribution.

As a result, the reported coverage for my testing of the code looks like it is below 80%, but it is actually above 80% in reality. I will show proof of this in the report. 

This metric indicates that a significant portion of my code has been tested, helping ensure quality and reliability in my contributions to the group project.
# Test Coverage Report

This document outlines the steps to run a bash script for generating a test coverage report for the project. The coverage report is specifically tailored to measure the code I have written as part of a group project.

## Pre-requisites

- Java JDK 17 or higher
- Gradle
- Bash environment (Linux/MacOS or Git Bash/WSL for Windows)

## Steps to Generate Test Coverage Report

### For Linux and macOS:

1. **Open Terminal**: Open a Terminal window in your project's root directory.

2. **Grant Permission**: Ensure that the script is executable by running:
    ```bash
    chmod +x run_coverage.sh
    ```

3. **Run the Script**: Execute the script to generate the test coverage report:
    ```bash
    ./run_coverage.sh
    ```

4. **View Report**: After the script runs successfully, open `build/customJacocoReportDir/test/html/index.html` in your web browser to view the coverage report.

### For Windows:

1. **Open Bash Environment**: Open Git Bash or Windows Subsystem for Linux (WSL) in your project's root directory. You can also use any Bash terminal provided by your IDE.

2. **Run the Script**: Execute the script to generate the test coverage report:
    ```bash
    bash ./run_coverage.sh
    ```

3. **View Report**: After the script runs successfully, navigate to `build/customJacocoReportDir/test/html/index.html` in your file explorer and open it with your web browser to view the coverage report.