Feature: Update Todo
  As a user
  I would like to update todos
  So that I can keep them reflective of my tasks.

  Background:
    Given the API is responsive
    And the database contains the default objects

  Scenario: Update a todo with POST (Normal Flow)
    When the user updates the todo with id 1 to have title <title>, description <description>, and done status <doneStatus> via POST
    Then status code 200 is received
    And a todo object exists with title <title>, description <description>, and done status <doneStatus>

  Scenario: Update a todo with PUT (Alternate Flow)
    When the user updates the todo with id 1 to have title <title>, description <description>, and done status <doneStatus> via PUT
    Then status code 200 is received
    And a todo object exists with title <title>, description <description>, and done status <doneStatus>

  Scenario: Update a todo to have no information (Error Flow)
    When the user sends a POST request for the todo with id 1 with no body
    Then status code 400 is received
    And the error message is "title: field is mandatory"