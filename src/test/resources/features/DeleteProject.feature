Feature: Delete Project
  As a user, I want to delete a project so that I can remove it from the list of created projects.

  Background:
    Given the service is running

  # Normal Flow
  Scenario: Delete an existing project successfully
    When I send a DELETE request to "/projects/1"
    Then I should receive a response status code of 200
    And the project at "/projects/<id>" should be deleted

  # Alternative Flow
  Scenario Outline: Delete a project after creating a new one
    When I send a POST request to "projects" with title "title" and description "description"
    And I send a DELETE request to "/projects/<id>"
    Then I should receive a response status code of 200

    Examples:
      | id | title       | description                   |
      | 1  | Project 1   | New description of Project 1  |
      | 2  | Project 2   | New description of Project 2  |

  # Error Flow
  Scenario: Delete a non-existing project
    When I send a DELETE request to "/projects/1111"
    Then I should receive a response status code of 404
    And the response should contain the error message "Could not find any instances with projects/1111"