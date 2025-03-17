Feature: View_Project
  As a user
  I would like to view a project
  So that I see the progress on it.

  Background: 
    Given the API is responsive
    And the database contains the default objects
    And that a project with id 1 exists in the database
    And that the project with id 25 is nonexistant

  Scenario: View a single todo by ID with JSON format (Normal Flow)
    When the user attempts to view the todo with id 1 via JSON payload
    Then the satus code 200 is received
    And the project with id 1 is returned in JSON format
    
   Scenario: View a single todo by ID with XML format (Alternative Flow)
     When the user attempts to view the todo with id 1 via XML payload
     Then the satus code 200 is received
     And the project with id 1 is returned in XML format

   Scenario: View a nonexistant project (Error Flow)
    When the user attempts to view the todo with id 2 via JSON payload
    Then the satus code 404 is received
    And the error message is "Could not find an instance with projects/25"
     
