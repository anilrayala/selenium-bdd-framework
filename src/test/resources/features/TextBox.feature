Feature: Text Box Elements

  Scenario Outline: Submit Text Box form with multiple inputs
    Given user navigates to the textbox page
    When user enters "<name>", "<email>", "<currentAddress>", "<permanentAddress>"
    And user clicks on submit
    Then verify submitted details "<name>", "<email>", "<currentAddress>", "<permanentAddress>"

    Examples:
      | name        | email           | currentAddress | permanentAddress |
      | Anil Kumar  | anil@test.com   | Hyderabad      | Bengaluru        |
      | Test User   | test@test.com   | Chennai        | Mumbai           |
