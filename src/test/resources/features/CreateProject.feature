Feature: Create New Project
  As a user, I want to create a new project so that I can assign tasks to it

  Background:
    Given the service is running

    # Normal Flow
    Scenario Outline: Create a new project successfully with a title and description
      When I send a POST request to "projects" with title "title" and description "description"
      Then I should receive a response status code of 201
      And the response should contain a project with title "title" and description "description"

      Examples:
      | title        | description               |
      | Project 1    | Description of Project 1  |
      | Project 2    | Description of Project 2  |

  # Alternate Flow
  Scenario: Create a project with an existing title
    Given a project with the title "Test Project" already exists
    When I send a POST request to "projects" with title "My Test Project" and description "Test description"
    Then I should receive a response status code of 201
    And the response should contain a project with title "My Test Project" and description "Test description"

  # Error Flow
  Scenario: Create a project with a specified id
    When I send a POST request to "projects" with id "1" and title "My New Project" and description "New description"
    Then I should receive a response status code of 400
    And the response should contain the error message "Invalid Creation: Failed Validation: Not allowed to create with id"