Feature: Student Registration Form Using Excel Data

  As a user
  I want to fill out the student registration form
  So that I can verify all form fields are populated correctly

@excel
Scenario Outline: Fill student registration form using excel
  Given user fills student form using row "<row>" from "StudentForm" sheet
  When user uploads profile picture "Photo.JPG"
  And user selects date of birth "08" "August" "1997" and select gender hobby
  Then student form should be populated correctly using excel data

Examples:
  | row |
  | 1   |
  | 2   |
