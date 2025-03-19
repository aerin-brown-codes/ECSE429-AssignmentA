Feature: View_Project
  As a user
  I would like to view a project
  So that I see the progress on it.

  Background: 
    Given the API can operate 
    And the database contains the given objects  

  Scenario: View a single todo by ID with JSON format (Normal Flow)
    When the user attempts to view the project with id 1 via JSON payload
    Then the status code 200 is layed out
    And the project with id 1 is returned in JSON format
    
   Scenario: View a single todo by ID with XML format (Alternative Flow)
     When the user attempts to view the project with id 1 via XML payload
     Then the status code 200 is layed out
     And the project with id 1 is returned in XML format

   Scenario: View a nonexistant project (Error Flow)
    When the user attempts to view the project with id 25 via JSON payload
    Then the status code 404 is demonstrated
    And the error message is "Could not find an instance with projects/25"
     
