Feature: Update Todo
  As a user
  I would like to delete todos
  So that I can get rid of todos I no longer need.

  Background:
    Given the API is responsive
    And the database contains only the default objects

  Scenario: Delete a todo with JSON (Normal Flow)
    When the user attempts to delete the todo with id 1 with a JSON payload
    Then status code 200 is received
    And no todo with id 1 exists

  Scenario: Delete a todo with XML (Alternate Flow)
    When the user attempts to delete the todo with id 1 with a XML payload
    Then status code 200 is received
    And no todo with id 1 exists

  Scenario: Delete a nonexistent todo (Error Flow)
    When the user attempts to delete the todo with id 25 with a JSON payload
    Then status code 404 is received
    And the error message is Could not find any instances with todos/25