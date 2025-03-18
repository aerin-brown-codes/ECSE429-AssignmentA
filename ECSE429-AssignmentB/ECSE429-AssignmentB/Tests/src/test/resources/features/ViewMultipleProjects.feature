Feature: View Multiple Todos
  As a user
  I would like to view multiple projects simultaneously
  So that I can get tailor my research.

  Background:
    Given the API is responsive
     And the database contains only the default objects

  Scenario: View all projects (Normal Flow)
    When the user attempts to view all projects
    Then status code 200 is received
    And the project with id 1 is returned

  Scenario: View filtered projects (Alternate Flow)
    When the user attempts to view only projects with active of value false
    Then status code 200 is received
    And the project with id 1 is returned 

  Scenario: Filter by nonexistant parameter (Error Flow)
    When the user attempts to view only projects with blah of value blah
    Then status code 200 is received
    And the project with id 1 is returned
