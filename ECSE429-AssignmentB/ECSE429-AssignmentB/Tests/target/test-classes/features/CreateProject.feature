Feature: Create New Project
  As a user
  I would like to create projects
  So that I can organize my tasks.

  Background:
    Given the API is responsive
    And the database contains the default objects

  Scenario: Create a new Project with JSON (Normal Flow)
      When the user creates a new project with title "House work" and description "Household chores" in "JSON" format.
      Then status code 201 is visible
      And a new project is received with title "House work", description "Household chores", id 2, completed "false" and active "false" in "JSON" format.

  Scenario: Create a new Project with XML (Alternative Flow)
      When the user creates a new project with title "House work" and description "Household chores" in "XML" format.
      Then status code 201 is visible
      And a new project is received with title "House work", description "Household chores", id 2, completed "false" and active "false" in "XML" format.

 Scenario: Create a new project in JSON format but giving a XML format (Error Flow)
    When the user creates a new project with title "House work" and description "Household chores" in "XML" format.
    Then status code 400 is put on screen
    And the error message is ""java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 1 path $""

      
