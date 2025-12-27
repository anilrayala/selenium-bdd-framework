Feature: Radio Button Elements

  As a user
  I want to interact with radio button elements
  So that I can verify the selected options are displayed correctly

  Scenario: Select radio buttons and verify results
    Given user navigates to the radio button page
    When user clicks on the Yes radio button
    Then verify the success text is displayed as Yes
    When user clicks on the Impressive radio button
    Then verify the success text is displayed as Impressive
#    And verify the No radio button is disabled
