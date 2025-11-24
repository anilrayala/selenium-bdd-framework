# 📚 Selenium BDD Automation Framework

![Java](https://img.shields.io/badge/Java-23-orange)
![Selenium](https://img.shields.io/badge/Selenium-4.x-green)
![Cucumber](https://img.shields.io/badge/Cucumber-7.x-brightgreen)
![TestNG](https://img.shields.io/badge/TestNG-7.x-red)
![Build](https://img.shields.io/badge/Build-Maven-blue)

**Java + Selenium + Cucumber + TestNG + Extent Reports + Allure + GitHub Actions**

This repository contains a fully scalable, production-ready BDD automation framework designed for UI test automation with multi-environment execution, tag-based runs, retry logic, parallel execution, structured logging, screenshots, and CI/CD integration.

---

## 📖 Table of Contents
- [Features Overview](#-features-overview)
- [Project Structure](#-project-structure)
- [Environment Handling](#-environment-handling)
- [Running Tests (Local)](#-running-tests-locally-with-environment-selection)
- [Running with Tags](#-running-with-tags)
- [CI/CD with GitHub Actions](#-cicd-with-github-actions)
- [Reporting & Screenshots](#-screenshots--reporting)
- [Viewing Allure Reports](#-viewing-allure-reports-local--ci-artifacts)
- [Troubleshooting](#-troubleshooting)

---

## 🚀 Features Overview

* **Selenium WebDriver**: Automatic driver management via WebDriverManager (Chrome, Edge, Firefox).
* **Cucumber BDD + Gherkin**: Support for scenario outlines, examples, and tag-based execution (`@smoke`, `@regression`).
* **TestNG Execution Engine**: Parallel execution using the Cucumber TestNG runner.
* **Smart Environment Configurations**: Hierarchical config loading (System Property → Env Var → Properties file).
* **CI/CD Integration**: Fully configured GitHub Actions pipeline with artifact uploads and environment protection rules.
* **Retry Logic**: Automatically retries transient failures (timeouts, stale elements) while preserving assertion failures.
* **Comprehensive Reporting**: Integrated Extent Reports and Allure Reports with screenshots and failure logs.
* **Structured Logging**: Log4j2 + MDC implementation to correlate logs with specific scenario names.
* **Modern WaitHelper**: robust utility for `SafeClick`, `SafeType`, Angular waits, and stability checks.

---

## 📁 Project Structure

```text
selenium-bdd-framework/
├── src/main/java/
│   ├── base/                  # BaseTest class
│   ├── factory/               # DriverFactory (ThreadLocal WebDriver)
│   ├── pages/                 # Page Objects (POM)
│   ├── utils/                 # WaitHelper, ConfigReader, Retry, TestDataManager
│   └── reports/               # ExtentManager, ExtentTestManager
│
├── src/test/java/
│   ├── stepDefinitions/       # Cucumber Step Definitions
│   ├── runners/               # TestRunner (TestNG + Cucumber)
│   └── hooks/                 # Cucumber Hooks (Before/After)
│
├── src/test/resources/
│   ├── features/              # Feature files (Gherkin)
│   ├── testng.xml             # TestNG suite configuration
│   ├── testdata.json          # External Test data
│   ├── config-dev.properties  # Environment specific configs
│   ├── config-qa.properties
│   ├── config-prod.properties
│   └── log4j2.xml             # Logging configuration
│
├── reports/                   # Extent + screenshot outputs
├── pom.xml                    # Dependencies & plugins
└── .github/workflows/ci.yml   # GitHub Actions CI/CD pipeline
```

---

## 🌐 Environment Handling

The framework supports `dev`, `qa`, and `prod` environments. Configuration values are retrieved based on the following precedence order:

1.  **System Property** (`-Denv=qa`)
2.  **Environment Variable** (Used in CI for Secrets like `API_KEY`)
3.  **Property File** (`src/main/resources/config-<env>.properties`)

### ⚙️ Browser Configuration
Set the browser in `config-<env>.properties` or override via CLI:
```properties
browser=chrome
# Options: chrome, edge, firefox
```

---

## 🚀 Running Tests Locally with environment selection

### Basic Execution
**QA Environment**
```bash
mvn -Denv=qa test
```

**Dev Environment**
```bash
mvn -Denv=dev test
```

**Prod Environment (Headless Recommended)**
```bash
mvn -Denv=prod -Dheadless=true test
```

### Overriding Configs
You can override any property from the command line:
```bash
mvn -Denv=qa -Dbrowser=firefox -DretryCount=2 test
```

---

## 🏷️ Running with Tags

### Windows PowerShell
*Note: PowerShell requires single quotes for tag expressions.*
```powershell
mvn -Denv=qa -Dcucumber.filter.tags='@smoke' test
```

### Windows CMD / Linux / macOS
```bash
mvn -Denv=qa -Dcucumber.filter.tags="@smoke" test
```

### Complex Tag Expressions
```bash
# Run regression but exclude work-in-progress
mvn -Denv=qa -Dcucumber.filter.tags="@regression and not @wip" test

# Run sanity OR critical tests
mvn -Dcucumber.filter.tags="@sanity or @critical" test
```

---

## 📦 CI/CD with GitHub Actions

The pipeline is defined in `.github/workflows/ci.yml`.

### Triggers
1.  **Automatic:** On `push` and `pull_request` to `main`.
2.  **Manual:** `workflow_dispatch` allows selecting Environment and Tags via UI.

### 🔒 Security & Secrets
* **Prod Protection:** Automatic runs on `prod` are blocked. Prod deployment requires manual approval via GitHub Environments.
* **Secret Injection:** Secrets (API Keys, DB Passwords) are injected securely at runtime.
    * *Setup:* Go to Repo Settings → Environments → Add Secrets.

### 🛠 Artifacts
After execution, the following artifacts are uploaded:
* Extent HTML Report
* Allure Report History
* Screenshots folder

---

## 🖼️ Screenshots & Reporting

### 📊 Extent Reports
Located at: `reports/ExtentReport_<timestamp>.html`
* Contains Base64 inline images for failures.
* Logs steps per scenario.

### 📈 Allure Reports
Generated via `mvn allure:report`.
* Interactive charts, graphs, and timeline views.
* Retries are grouped under the "Retries" tab.

---

## 📊 Viewing Allure Reports (Local & CI Artifacts)

Browsers block local JavaScript/CSS when opening `index.html` directly. To view reports properly, serve them over a local HTTP server.

### ✅ Method 1: Java Simple Web Server (Recommended)
Since this project uses Java 23 (or 18+), use the built-in server:

1.  Download the `allure-report.zip` artifact from GitHub Actions and extract it.
2.  Open your terminal in the report directory:
    ```bash
    cd "C:\path\to\extracted\allure-report\site\allure-maven-plugin"
    ```
3.  Run the server:
    ```bash
    java -m jdk.httpserver
    ```
4.  Open **http://localhost:8000** in your browser.

### 🟩 Method 2: Allure CLI (Optional)
If you have Allure installed locally:
```bash
allure serve target/allure-results
```

---

## 🛠️ Components & Architecture

### Test Runner
* Supports **Parallel Execution**.
* Integrates `TestRetryAnalyzer` listener.

### Retry Analyzer
* Retries **only transient failures** (Timeouts, StaleElements, Session crashes).
* **Ignores** hard assertion failures (logic bugs).
* Captures screenshots on every retry attempt.

### WaitHelper (Stability)
Avoids flaky tests by implementing smart waits:
* `waitForElementToBeStable`
* `waitForAngularToLoad`
* `safeClick` / `safeType`

---

## ❗ Troubleshooting

| Issue | Solution |
| :--- | :--- |
| **Tags ignored in PowerShell** | Use single quotes: `-Dcucumber.filter.tags='@tag'` |
| **Browser not launching in CI** | Ensure `browser=` is set in `config-<env>.properties`. CI installs browser based on this value. |
| **Prod run skipped** | Automatic triggers skip Prod for safety. Use the **"Run Workflow"** button manually. |
| **Secrets missing** | Add them in GitHub: Settings → Environments → `dev/qa/prod` → Secrets. |
| **Blank Allure Report** | Do not double-click `index.html`. Use `java -m jdk.httpserver` to view it. |

