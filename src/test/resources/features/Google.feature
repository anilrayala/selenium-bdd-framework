Feature: Google Page Verification

  Scenario: Verify Google Home Page Title
    Given user launches the browser
    When user opens the Google homepage
    Then the page title should contain "Google"