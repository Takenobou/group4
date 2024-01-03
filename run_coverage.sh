#!/bin/bash

# Run the Gradle task to clean, test, and generate the Jacoco test report
./gradlew clean test jacocoTestReport
