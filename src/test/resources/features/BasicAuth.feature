Feature: Basic Authentication Verification

  Scenario: Verify login via Basic Auth popup
    Given user navigates to the internet home page
    When user clicks on the Basic Auth link
    And user enters username "admin" and password "admin" in the popup
    Then verify the Basic Auth success message is displayed

