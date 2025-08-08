Feature: Login functionality

  Scenario: Successful login with valid credentials
    Given I launch the browser
    When I navigate to the login page
    And I enter valid username and password
    And I click the login button
    Then I should be redirected to the dashboard page