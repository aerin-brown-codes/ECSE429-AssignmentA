Feature: View Multiple Todos
  As a user
  I would like to view multiple todos simultaneously
  So that I can get see view related tasks in a group.

  Background:
    Given the API is ready to go
    And the database contains the initial objects

  Scenario: View all todos (Normal Flow)
    When the user attempts to view all todos
    Then status code 200 is demonstrated
    And the todos with ids 1 and 2 are returned

  Scenario: View filtered todos (Alternate Flow)
    When the user attempts to view only todos with title of value file paperwork
    Then status code 200 is demonstrated
    And the todo with id 2 is returned 

  Scenario: Filter by nonexistant parameter (Error Flow)
    When the user attempts to view only todos with blah of value blah
    Then status code 200 is demonstrated
    And the todos with ids 1 and 2 are returned