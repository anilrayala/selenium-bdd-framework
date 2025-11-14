Feature: Basic Authentication Verification

  Scenario: Verify login via Basic Auth popup
    Given user navigates to the internet home page
    When user clicks on the Basic Auth link
    And user enters username "admin" and password "admin" in the popup
    Then verify the Basic Auth success message is displayed

  Scenario: Verify Add or Remove elements
    Given user navigates to the internet home page
    When user clicks on the Add or Remove Elements link
    And user clicks on Add Element
    Then verify the Element added
    And user click on Delete
    Then verify the Element is deleted

#  Scenario: Verify click on checkbox
#    Given user navigates to the internet home page
#    When user clicks on checkboxes link
#    Then verify the checkbox is checked
#
#  Scenario: Verify Drag and Drop
#    Given user navigates to the internet home page
#    When user clicks on Drag and Drop link
#    Then user drag and drops element A into element B
#
#  Scenario: Verify Dropdown
#    Given user navigates to the internet home page
#    When user clicks on Dropdown link
#    Then user selects option1 from the dropdown list
