Feature: Get Project
  As a user, I want to get a specific project so that I retrieve its details

  Background:
    Given the service is running

  # Normal Flow
  Scenario Outline: Get an existing project successfully
    When I send a GET request to "/projects/<id>"
    Then I should receive a response status code of 200

    Examples:
      | id | title       | description                   |
      | 1  | Project 1   | New description of Project 1  |

  # Alternative Flow
  Scenario Outline: Get a project with an id filter after creating a new one
    When I send a POST request to "projects" with title "title" and description "description"
    And I send a GET request to "/projects/<id>" using filter "?id=<ID>"
    Then I should receive a response status code of 200

    Examples:
      | id | title       | description                   |
      | 1  | Project 1   | New description of Project 1  |
      | 2  | Project 2   | New description of Project 2  |

  # Error Flow
  Scenario: Get a non-existing project
    When I send a GET request to "/projects/11"
    Then I should receive a response status code of 404
    And the response should contain the error message "Could not find an instance with projects/11"