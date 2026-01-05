Feature: Student Registration Form using Json Data

  As a user
  I want to fill out the student registration form
  So that I can verify the form fields are populated correctly

@json
Scenario Outline: Fill student registration form using json
  Given user fills student form using json "studentForm.json" with key "<studentKey>"
  Then student form text fields should be populated correctly using json data

Examples:
  | studentKey |
  | student1   |
  | student2   |
