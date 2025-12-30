Feature: Button Elements

  As a user
  I want to interact with different button actions
  So that I can verify the corresponding messages are displayed

  Scenario: Perform button actions and verify messages
    Given user navigates to the buttons page
    When user performs double click on the button
    Then verify double click message is displayed
    When user performs right click on the button
    Then verify right click message is displayed
    And user performs dynamic click on the button and verify message
