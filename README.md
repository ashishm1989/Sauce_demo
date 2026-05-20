# SauceDemo UI Automation Framework

A UI test automation framework for [saucedemo.com](https://www.saucedemo.com) built with **Selenium WebDriver 4**, **TestNG**, **Java 11**, and **ExtentReports**.

---

## Table of Contents

1. [Framework Architecture](#framework-architecture)
2. [Project Structure](#project-structure)
3. [Design Patterns](#design-patterns)
4. [Prerequisites](#prerequisites)
5. [Setup](#setup)
6. [Running Tests](#running-tests)
7. [Configuration](#configuration)
8. [Test Reports](#test-reports)
9. [Key Features](#key-features)
10. [Test Coverage](#test-coverage)
11. [Troubleshooting](#troubleshooting)

---

## Framework Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Test Layer                           │
│  LoginTest  |  CartTest  |  E2ECheckoutTest  |  ...    │
└─────────────────────┬───────────────────────────────────┘
                      │ extends
┌─────────────────────▼───────────────────────────────────┐
│                   BaseTest                              │
│  @BeforeMethod: initDriver()  @AfterMethod: quitDriver()│
└──────────┬──────────────────────────┬───────────────────┘
           │ uses                     │ listens
┌──────────▼──────────┐   ┌──────────▼───────────────────┐
│    Page Objects     │   │       Listeners               │
│  LoginPage          │   │  TestListener (screenshots)   │
│  InventoryPage      │   │  ExtentReportListener (HTML)  │
│  CartPage           │   │  RetryTransformer (retries)   │
│  CheckoutPage       │   └──────────────────────────────┘
│  OrderConfirmPage   │
└──────────┬──────────┘
           │ uses
┌──────────▼──────────────────────────────────────────────┐
│                    Utilities                            │
│  DriverFactory  |  WaitUtils  |  ScreenshotUtil        │
│  ConfigReader   |  CsvDataProvider                     │
└─────────────────────────────────────────────────────────┘
```

---

## Project Structure

```
saucedemo-automation/
│
├── pom.xml                                # Maven dependencies & plugins
│
├── src/
│   ├── main/java/
│   │   ├── pages/
│   │   │   ├── BasePage.java              # Parent POM – shared helpers
│   │   │   ├── LoginPage.java             # Login page (/)
│   │   │   ├── InventoryPage.java         # Products page (/inventory.html)
│   │   │   ├── CartPage.java              # Cart page (/cart.html)
│   │   │   ├── CheckoutPage.java          # Checkout step 1 & 2
│   │   │   └── OrderConfirmationPage.java # Order complete (/checkout-complete.html)
│   │   │
│   │   └── utils/
│   │       ├── ConfigReader.java          # Reads config.properties (singleton)
│   │       ├── DriverFactory.java         # Thread-local WebDriver factory
│   │       ├── WaitUtils.java             # Explicit wait helpers
│   │       ├── ScreenshotUtil.java        # Screenshot capture on failure
│   │       └── CsvDataProvider.java       # CSV → TestNG DataProvider
│   │
│   └── test/
│       ├── java/
│       │   ├── tests/
│       │   │   ├── BaseTest.java          # @BeforeMethod / @AfterMethod setup
│       │   │   ├── LoginTest.java         # Login happy-path & negative tests
│       │   │   ├── InventoryTest.java     # Product listing & cart badge tests
│       │   │   ├── CartTest.java          # Cart CRUD tests
│       │   │   ├── E2ECheckoutTest.java   # Full checkout journey
│       │   │   └── DataDrivenLoginTest.java # CSV-driven login scenarios
│       │   │
│       │   └── listeners/
│       │       ├── TestListener.java      # Console logging + screenshot on fail
│       │       ├── ExtentReportListener.java # HTML report generation
│       │       ├── RetryAnalyzer.java     # Per-test retry logic
│       │       └── RetryTransformer.java  # Injects retry into all tests globally
│       │
│       └── resources/
│           ├── config.properties          # Central configuration
│           ├── testng.xml                 # Suite definition & test groups
│           ├── logback-test.xml           # Logging configuration
│           └── testdata/
│               └── login_data.csv         # CSV test data for data-driven tests
│
└── reports/                               # Generated after a test run
    ├── ExtentReport.html                  # Interactive HTML report
    ├── automation.log                     # Full test run log
    └── screenshots/                       # Failure screenshots (PNG)
```

---

## Design Patterns

### Page Object Model (POM)
Every web page has a dedicated class under `pages/`. Each class:
- Declares `@FindBy` locators as private fields
- Exposes **action methods** (`loginAs()`, `addProductToCart()`) not raw Selenium calls
- Returns the **next page object** from navigation methods for fluent chaining

```java
// Clean test code using POM
InventoryPage inventory = new LoginPage()
    .open()
    .loginAs("standard_user", "secret_sauce");
```

### Factory Pattern — DriverFactory
`DriverFactory` uses a `ThreadLocal<WebDriver>` to provide an isolated driver per thread, enabling future parallel execution without test interference.

### Singleton — ConfigReader
`ConfigReader` loads `config.properties` exactly once and caches values. System properties override file values, so `-Dbrowser=firefox` works from Maven without touching the file.

### Observer — Listeners
`TestListener` and `ExtentReportListener` implement `ITestListener`, reacting to TestNG lifecycle events without polluting test code.

---

## Prerequisites

| Tool        | Minimum Version | Notes                                |
|-------------|----------------|--------------------------------------|
| Java JDK    | 11             | Set `JAVA_HOME`                      |
| Maven       | 3.8            | Or use the included `mvnw` wrapper   |
| Google Chrome | Latest       | WebDriverManager auto-downloads driver |
| Git         | Any            |                                      |

---

## Setup

### 1. Clone the repository
```bash
git clone https://github.com/your-org/saucedemo-automation.git
cd saucedemo-automation
```

### 2. Verify Java
```bash
java -version          # Should print 11+
echo $JAVA_HOME        # Should point to your JDK
```

### 3. Install dependencies
```bash
mvn clean install -DskipTests
```
Maven downloads Selenium, TestNG, WebDriverManager, ExtentReports, etc.

### 4. (Optional) Configure
Edit `src/test/resources/config.properties` to override:
- `browser` — chrome / firefox / edge
- `headless` — true / false
- `products.to.add` — comma-separated product names

---

## Running Tests

### Run all tests
```bash
mvn clean test
```

### Run a specific group
```bash
# Smoke tests only
mvn clean test -Dgroups=smoke

# E2E checkout only
mvn clean test -Dgroups=e2e

# Cart tests only
mvn clean test -Dgroups=cart

# Data-driven login tests
mvn clean test -Dgroups=datadriven
```

### Override browser at runtime
```bash
mvn clean test -Dbrowser=firefox
mvn clean test -Dbrowser=edge
mvn clean test -Dbrowser=chrome -Dheadless=true
```

### Run a single test class
```bash
mvn clean test -Dtest=E2ECheckoutTest
mvn clean test -Dtest=LoginTest#validLoginShouldOpenInventoryPage
```

### Run in headless mode (e.g. CI)
```bash
mvn clean test -Dheadless=true
```

---

## Configuration

All settings live in `src/test/resources/config.properties`.  
Any property can be overridden with a Maven `-D` flag.

| Property               | Default              | Description                          |
|------------------------|----------------------|--------------------------------------|
| `base.url`             | https://www.saucedemo.com | Application URL               |
| `valid.username`       | standard_user        | Login username                       |
| `valid.password`       | secret_sauce         | Login password                       |
| `browser`              | chrome               | chrome / firefox / edge              |
| `headless`             | false                | Run browser headlessly               |
| `explicit.wait`        | 15                   | Seconds for explicit waits           |
| `implicit.wait`        | 5                    | Seconds for implicit waits           |
| `page.load.timeout`    | 30                   | Page load timeout (seconds)          |
| `screenshot.on.failure`| true                 | Capture PNG on test failure          |
| `screenshot.dir`       | reports/screenshots  | Where screenshots are saved          |
| `report.path`          | reports/ExtentReport.html | HTML report output             |
| `retry.count`          | 2                    | Retry attempts for failed tests      |
| `products.to.add`      | Sauce Labs Backpack, Sauce Labs Bike Light | Cart items |
| `checkout.firstname`   | John                 | Checkout form first name             |
| `checkout.lastname`    | Doe                  | Checkout form last name              |
| `checkout.zipcode`     | 12345                | Checkout form postal code            |

---

## Test Reports

After a test run, open:

```
reports/ExtentReport.html
```

The report includes:
- ✅ Pass / ❌ Fail / ⚠ Skip counts with duration
- Per-test log messages
- **Embedded screenshots** for every failed test
- System info (OS, Java version, browser, base URL)
- Test category tags (smoke, e2e, cart, datadriven)

Console and file logs are written to `reports/automation.log`.

---

## Key Features

### ✅ Page Object Model
All locators and interactions are encapsulated in page classes. Tests read like plain English.

### ✅ Explicit Waits
`WaitUtils` provides typed wait helpers (`waitForVisible`, `waitForClickable`, `waitForUrlContains`) throughout, eliminating `Thread.sleep` usage.

### ✅ Thread-safe Driver Management
`DriverFactory` uses `ThreadLocal<WebDriver>` so tests are isolation-ready for parallel execution.

### ✅ Screenshot on Failure
`TestListener.onTestFailure()` calls `ScreenshotUtil.capture()` automatically and embeds the image in the ExtentReport.

### ✅ Data-Driven Testing
`DataDrivenLoginTest` uses `@DataProvider` fed by `CsvDataProvider.loadCsv()`. Add rows to `login_data.csv` to test new scenarios without touching Java code.

### ✅ Retry Mechanism
`RetryAnalyzer` retries failed tests up to `retry.count` times (default: 2).  
`RetryTransformer` injects it globally — no per-test annotation needed.

### ✅ Zero Hardcoding
Credentials, URLs, timeouts, product names, and checkout data all come from `config.properties` or environment-variable overrides.

### ✅ Structured Logging
SLF4J + Logback writes structured logs to console and rolling file at `reports/automation.log`.

---

## Test Coverage

| Test Class              | Scenarios Covered                                               |
|-------------------------|-----------------------------------------------------------------|
| `LoginTest`             | Valid login, locked-out user, wrong password, empty fields, logout |
| `InventoryTest`         | Product list loads, add/remove items, cart badge count          |
| `CartTest`              | Items present, count, remove item, continue shopping            |
| `E2ECheckoutTest`       | Full checkout happy path, missing-field validation, back-to-products |
| `DataDrivenLoginTest`   | 8 credential combinations from CSV (success + failure paths)    |

---

## Troubleshooting

**ChromeDriver / browser version mismatch**  
WebDriverManager auto-downloads the correct driver. If it fails due to network restrictions, download manually from https://chromedriver.chromium.org and set `webdriver.chrome.driver` system property.

**Tests time out on slow CI machines**  
Increase `explicit.wait` and `page.load.timeout` in `config.properties` or pass `-Dexplicit.wait=30`.

**Screenshots not appearing in report**  
Ensure `reports/screenshots/` directory is writable. Check `screenshot.on.failure=true` in config.

**Port conflicts on headless Chrome**  
Add `--remote-debugging-port=0` to `ChromeOptions` in `DriverFactory`.

**Running behind a corporate proxy**  
Set `JAVA_OPTS="-Dhttps.proxyHost=proxy.corp.com -Dhttps.proxyPort=8080"` before running Maven.
