<<<<<<< HEAD
# SauceDemo UI Automation Framework

A UI test automation framework for [saucedemo.com](https://www.saucedemo.com) built with Selenium WebDriver 4, TestNG, Java 11, and ExtentReports.

---

## Framework Architecture

The framework is organized into three layers.

The test layer contains all test classes (LoginTest, CartTest, E2ECheckoutTest, etc.) which extend BaseTest. BaseTest handles driver setup and teardown via @BeforeMethod and @AfterMethod.

The page object layer contains one class per page (LoginPage, InventoryPage, CartPage, CheckoutPage, OrderConfirmationPage). Each class encapsulates locators and actions for that page.

The utility layer provides shared helpers used across the framework: DriverFactory, WaitUtils, ScreenshotUtil, ConfigReader, and CsvDataProvider.

Listeners run alongside tests and handle cross-cutting concerns: TestListener captures screenshots on failure, ExtentReportListener generates the HTML report, and RetryTransformer injects retry logic globally.

---

## Project Structure

pom.xml

src/main/java/pages/
- BasePage.java
- LoginPage.java
- InventoryPage.java
- CartPage.java
- CheckoutPage.java
- OrderConfirmationPage.java

src/main/java/utils/
- ConfigReader.java
- DriverFactory.java
- WaitUtils.java
- ScreenshotUtil.java
- CsvDataProvider.java

src/test/java/tests/
- BaseTest.java
- LoginTest.java
- InventoryTest.java
- CartTest.java
- E2ECheckoutTest.java
- DataDrivenLoginTest.java

src/test/java/listeners/
- TestListener.java
- ExtentReportListener.java
- RetryAnalyzer.java
- RetryTransformer.java

src/test/resources/
- config.properties
- testng.xml
- logback-test.xml
- testdata/login_data.csv

reports/
- ExtentReport.html
- automation.log
- screenshots/

---

## Prerequisites

- Java JDK 11 or higher (JAVA_HOME must be set)
- Maven 3.8 or higher
- Google Chrome (latest)
- Git

---

## Setup

Clone the repository and install dependencies.

```bash
git clone https://github.com/your-org/saucedemo-automation.git
cd saucedemo-automation
mvn clean install -DskipTests
```

All settings are in `src/test/resources/config.properties`. Edit this file to change the browser, credentials, timeouts, or test data.

---

## Running Tests

Run all tests:
```bash
mvn clean test
```

Run a specific group:
```bash
mvn clean test -Dgroups=smoke
mvn clean test -Dgroups=e2e
mvn clean test -Dgroups=cart
mvn clean test -Dgroups=datadriven
```

Run a specific test class or method:
```bash
mvn clean test -Dtest=E2ECheckoutTest
mvn clean test -Dtest=LoginTest#validLoginShouldOpenInventoryPage
```

Run with a different browser:
```bash
mvn clean test -Dbrowser=firefox
mvn clean test -Dbrowser=edge
```

Run in headless mode (for CI):
```bash
mvn clean test -Dheadless=true
```

---

## Test Reports

After a test run, open `reports/ExtentReport.html` in a browser. It shows pass/fail/skip counts, per-test logs, and embedded screenshots for failures. Full logs are written to `reports/automation.log`.
=======
# Sauce_demo
Sauce_demo_UI_Automation Framework
>>>>>>> 2daf2f0317b5a961cf4f6c6ea3c466017bb0b201
