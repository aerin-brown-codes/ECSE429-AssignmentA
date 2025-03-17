Feature: Create New Todo
  As a user
  I would like to create todos
  So that I can track new tasks.

  Background:
    Given the API is responsive
    And the database contains the default objects

  Scenario: Create a new todo with JSON (Normal Flow)
    When the user creates a new todo with title <title>, description <description>, and done status <doneStatus> in JSON format
    Then status code 201 is received
    And a todo object exists with title <title>, description <description>, and done status <doneStatus>

  Scenario: Create a new todo with XML (Alternate Flow)
    When the user creates a new todowith title <title>, description <description>, and done status <doneStatus> in XML format
    Then status code 201 is received
    And a todo object exists with title <title>, description <description>, and done status <doneStatus>


  Scenario: Create todo with no title (Error Flow)
    When the user attempts to create an empty todo 
    Then status code 400 is received
    And the error message is "title: field is mandatory"