Feature: Create New Todo
  As a user
  I would like to create todos
  So that I can track new tasks.

  Background:
    Given the API can respond
    And the database has the default objects

  Scenario: Create a new todo with JSON (Normal Flow)
    When the user creates a new todo with title wash dishes, description run dishwasher, and done status false in JSON format
    Then status code 201 is received
    And a todo object is received with title wash dishes, description run dishwasher, and done status false in JSON format

  Scenario: Create a new todo with XML (Alternate Flow)
    When the user creates a new todo with title wash dishes, description run dishwasher, and done status false in XML format
    Then status code 201 is received
    And a todo object is received with title wash dishes, description run dishwasher, and done status false in XML format


  Scenario: Create todo with no title (Error Flow)
    When the user attempts to create an empty todo 
    Then status code 400 is shown
    And the error message is title: field is mandatory