Feature: Delete Project
  As a user
  I would like to delete project
  So that I can get rid of projects I no longer need.

  Background:
    Given the API is active
    And the database only contains the default objects

  Scenario: Delete a project with JSON (Normal Flow)
    When the user attempts to delete the project with id 1 with a JSON payload
    Then the status code 200 is received 
    And no project with id 1 exists

  Scenario: Delete a project with XML (Alternate Flow)
    When the user attempts to delete the project with id 1 with a XML payload
    Then the status code 200 is received 
    And no project with id 1 exists

  Scenario: Delete a nonexistent project (Error Flow)
    When the user attempts to delete the project with id 25 with a JSON payload
    Then status code 404 is received 
    And the error is Could not find any instances with projects\/{int}
