Feature: Student Registration Form Using Excel Data

  As a user
  I want to fill out the student registration form
  So that I can verify the form fields are populated correctly

@excel
Scenario Outline: Fill student registration form using excel
Given user fills student form using row "<row>" from "StudentForm" sheet
Then student form text fields should be populated correctly using excel data

Examples:
  | row |
  | 1   |
  | 2   |
