# selenium-bdd-framework

Architecture & folder structure
Environment handling
CI/CD
Browsers & configuration
Tags (smoke/regression)
Retry logic
Logging
Reporting
Commands (local & CI)
Secrets
Screenshots
Troubleshooting

#### 📚 Selenium BDD Automation Framework
Java + Selenium + Cucumber + TestNG + Extent Reports + Allure + GitHub Actions

This repository contains a fully scalable, production-ready BDD automation framework designed for UI test automation with multi-environment execution, tag-based runs, retry logic, parallel execution, structured logging, screenshots, and CI/CD integration.

#### 🚀 Features Overview
✔ Selenium WebDriver (Chrome, Edge, Firefox)
Automatic driver management via WebDriverManager.

✔ Cucumber BDD + Gherkin
Support for scenario outlines, examples, and tag-based execution (@smoke, @regression, etc.).

✔ TestNG Execution Engine
Parallel execution with Cucumber TestNG runner.

✔ Environment Configurations (dev, qa, prod)
Uses hierarchical precedence:
1. System Property → 2. Environment Variable → 3. config-<env>.properties

✔ CI/CD with GitHub Actions
Multi-environment selection
Tag-based runs
Browser installation per config
Environment-scoped secrets
Artifact upload (reports, screenshots, summaries)

✔ Retry Logic (Transient Failures)
Automatically retries tests on timeouts, stale elements, session crashes.

✔ Extent Reports + Allure Reports
Screenshots, test summaries, step logs, failures, retries.

✔ Structured Logging (Log4j2 + MDC)
Logs correlated with scenario names for easy debugging.

✔ Modern WaitHelper
SafeClick, SafeType, visibility waits, page load waits, Angular/JQuery waits.

### 📁 Project Structure
    selenium-bdd-framework/
        src/main/java/
                    base/                  # BaseTest class
                    factory/               # DriverFactory (ThreadLocal WebDriver)
                    pages/                 # Page Objects
                    utils/                 # WaitHelper, ConfigReader, Retry, TestDataManager
                    reports/               # ExtentManager, ExtentTestManager
    
        src/test/java/
                    stepDefinitions/       # Cucumber Step Definitions
                    runners/               # TestRunner (TestNG + Cucumber)
                    hooks/                 # Cucumber Hooks (Before/After)
    
        src/test/resources/
                    features/              # Feature files (Gherkin)
                    testng.xml             # TestNG suite
                    testdata.json          # Test data
    
        src/main/resources/
                    config-dev.properties
                    config-qa.properties
                    config-prod.properties
                    log4j2.xml
    
        reports/                   # Extent + screenshot outputs
        pom.xml                    # Dependencies & plugins
        .github/workflows/ci.yml   # GitHub Actions CI/CD pipeline


#### 🌐 Environment Handling
The framework supports dev, qa, prod environments.

#### 🔥 Environment selection precedence
When retrieving configuration values:

#### 1️⃣ System Property
mvn -Denv=qa -Dheadless=true test

#### 2️⃣ Environment Variable (API_KEY, DB_PASSWORD, HEADLESS)
Used mainly in CI.

#### 3️⃣ Property file
src/main/resources/config-<env>.properties.

#### 🚀 Running tests locally with environment selection
▶ QA
mvn -Denv=qa test

▶ Dev
mvn -Denv=dev test

▶ Prod (headless recommended)
mvn -Denv=prod -Dheadless=true test


Override any config value:

mvn -Denv=qa -Dbrowser=firefox -DretryCount=2 test

#### 🏷️ Running with Tags (smoke, regression, etc.)
Windows PowerShell
mvn -Denv=qa -Dcucumber.filter.tags='@smoke' test

Windows CMD / Linux / macOS
mvn -Denv=qa -Dcucumber.filter.tags="@smoke" test

Multiple tag expressions
mvn -Denv=qa -Dcucumber.filter.tags="@regression and not @wip" test
mvn -Dcucumber.filter.tags="@sanity or @critical" test

#### 🧰 Test Runner

TestRunner.java:

Supports parallel execution

Logs active tags

Uses TestNG @DataProvider(parallel = true)

Integrates Allure + Extent + retry listener safely

#### 🔁 Retry Analyzer

TestRetryAnalyzer provides:

Retries only transient failures
(timeouts, stale elements, session crashes, WebDriverException)

Ignores assertion failures

Logs retry attempts in Extent & Allure

Captures screenshots for each retry

#### ⏳ WaitHelper (Stability)

WaitHelper includes:

SafeClick / SafeType

visibilityOf, clickable

presenceOfAllElements

waitForElementToBeStable

waitForAngularToLoad

waitForPageToLoad

stroke-safe timeouts

screenshots on wait failures

This avoids flaky tests.

🖼️ Screenshots & Reporting
📊 Extent Reports

Located at:

reports/ExtentReport_<timestamp>.html
reports/screenshots/


Includes:

Screenshot images

Base64 inline images

Logging per step

Pass/Fail/Skip summary

📈 Allure Reports

Generated in GitHub Actions using:

mvn allure:report

Artifacts uploaded automatically.

📦 CI/CD with GitHub Actions
Triggering:
Automatic on push and pull_request to main branch
Manual through workflow_dispatch with UI inputs:
Environment (dev, qa, prod)
Cucumber tags

Safety:

prod runs blocked on automatic triggers
Only allowed via manual Run workflow
GitHub Environments enforce approvals for prod

Browser auto-installation:

CI inspects config-<env>.properties → browser= and installs:
chromium-browser
microsoft-edge-stable
firefox

Artifact uploads:

Extent reports
Allure reports
Screenshot folder
summary.json

🔑 Secrets Management

Use GitHub Environment Secrets:
Settings → Environments → dev/qa/prod

Add secrets:
API_KEY
DB_PASSWORD

CI injects them securely:
env:
API_KEY: ${{ secrets.API_KEY }}
DB_PASSWORD: ${{ secrets.DB_PASSWORD }}

📜 Logging (Log4j2 + MDC)

log4j2.xml includes:

Timestamp
Scenario name via MDC
Log levels
Console + file logging (logs/framework.log)

Hooks automatically set ThreadContext.put("scenario", scenario.getName()).

🔍 Test Data

Stored in:
src/test/resources/testdata.json


Loaded via:
JSONObject data = TestDataManager.getData("login");
String username = data.getString("username");

💡 Browser Configuration

In config-<env>.properties:

browser=chrome
# or
browser=edge
browser=firefox


CI installs the correct browser binary automatically.

🧪 Example Feature Tags
@smoke @ui
Scenario: Validate login form
Given I navigate to the login page
...

@regression @critical
Scenario: Search for a product
...

🛠️ Useful Maven Commands
Clean + run tests
mvn clean test

Run only smoke tests in QA
mvn -Denv=qa -Dcucumber.filter.tags="@smoke" test

Generate Allure report locally
mvn allure:serve

❗ Troubleshooting
Tags not working?

PowerShell needs single quotes:
-Dcucumber.filter.tags='@smoke'

Browser not launching in CI?
Ensure browser=chrome|edge|firefox is defined in config-<env>.properties
CI auto-installs based on config

Prod run blocked automatically?
That means you triggered via push → expected
Use Run workflow UI for prod

No secrets found?
Add them under:
Settings → Environments → <env> → Secrets

Framework failing early in CI?
Check:
Running tests for env='qa' (event='push')
Confirm the selected environment.