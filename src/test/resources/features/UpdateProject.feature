Feature: Update Project
  As a user, I want to update a project so that I can change its details if needed.

  Background:
    Given the service is running

  Scenario Outline: Update a project's id, title and description successfully
    When I send a PUT request to "/projects/<id>" with title "Updated Project" and description "Updated Description"
    Then I should receive a response status code of 200
    And the response should contain a project with title "Updated Project" and description "Updated Description"

    Examples:
      | id | Updated Project  | Updated Description      |
      | 1  | New Proj1        | Updated Description1     |
      | 1  | New Proj2        | Updated Description2     |

  Scenario: Update a non-existing project
    When I send a PUT request to "/projects/-1" with title "title" and description "description"
    Then I should receive a response status code of 404
    And the response should contain the error message "Invalid GUID for -1 entity project"

  Scenario: Update a project with string value of a boolean
    When I send a PUT request to "/projects/:id" with active "True"
    Then I should receive a response status code of 400
    And the response should contain the error message "Expected BEGIN_OBJECT but was STRING"