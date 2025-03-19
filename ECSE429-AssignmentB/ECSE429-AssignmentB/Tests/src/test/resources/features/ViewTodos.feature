Feature: View Todos
  As a user
  I would like to view individual todos
  So that I can get review information about specific tasks.

  Background:
    Given the API is alert and ready
    And the database contains the initial default objects

  Scenario: View a single todo by ID with JSON (Normal Flow)
    When the user attempts to view the todo with id 1 via JSON payload
    Then status code 200 is visible
    And the todo with id 1 is returned in JSON format

  Scenario: View a single todo by ID with XML format (Alternate Flow)
    When the user attempts to view the todo with id 1 via XML payload
    Then status code 200 is visible
    And the todo with id 1 is returned in XML format

  Scenario: View a nonexistant todo (Error Flow)
    When the user attempts to view the todo with id 25
    Then status code 404 is layed out
    And the message is Could not find any instances with todos/25}
