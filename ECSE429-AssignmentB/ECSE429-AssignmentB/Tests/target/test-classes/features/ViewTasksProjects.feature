Feature: View Tasks of Project
  As a user
  I would like to see the tasks associated to a project
  So that I can get better organize myself.

  Background:
    Given the API is alive
    And the database contains the by default objects

  Scenario: View the tasks of project by ID with JSON format (Normal Flow)
    When the user attempts to view the tasks of project with id 1 via JSON payload
    Then the status code 200 is seen
    And the tasks of project with id 1 are returned in JSON format
    
   Scenario: View the tasks of project by ID with XML format (Alternative Flow)
     When the user attempts to view the tasks of project with id 1 via XML payload
     Then the status code 200 is seen
     And the tasks of project with id 1 are returned in XML format

   Scenario: View the tasks of a nonexistant project (Error Flow)
    When the user attempts to view the tasks of project with id 25 via JSON payload
    Then the status code 200 is seen
    And some tasks appear.

     
