Feature: Update Todos
  As a user
  I would like to update todos
  So that I can keep them reflective of my tasks.

  Background:
    Given the API is ok
    And the database has only the default objects

  Scenario: Update a todo with POST (Normal Flow)
    When the user updates the todo with id 1 to have title wash dishes, description run dishwasher, and done status false via POST
    Then status code 201 is readable
    And a todo object is received with id 3, title wash dishes, description run dishwasher, and done status false

  Scenario: Update a todo with PUT (Alternate Flow)
    When the user updates the todo with id 1 to have title wash dishes, description run dishwasher, and done status false via PUT
    Then status code 200 is readable
    And a todo object is received with id 1, title wash dishes, description run dishwasher, and done status false

  Scenario: Update a todo to have no information (Error Flow)
    When the user sends a PUT request for the todo with id 1 with no body
    Then status code 400 is returned for updating todo
    And the message is title: field is mandatory