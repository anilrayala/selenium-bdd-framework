Feature: Checkbox Elements

  As a user
  I want to interact with checkbox tree elements
  So that I can verify the selected options are displayed correctly

  Scenario: Click checkbox with multiple inputs and verify
    Given user navigates to the checkbox page
    When user clicks on the homeToggle and desktopToggle
    Then user clicks on commands checkbox and verify text
    And user clicks on documentsToggle and react checkbox
    Then verify the reactText
