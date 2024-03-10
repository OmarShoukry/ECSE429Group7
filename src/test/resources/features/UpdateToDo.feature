Feature: Update Existing ToDo
  As a user, I want to update an existing todo task so that I can modify its details

  Background:
    Given the service is running

  # Normal Flow
  Scenario Outline: Update a ToDo task's description successfully using PUT
    When I send a PUT request to "/todos/<ID>" using title: "title" and description: "description"
    Then I should receive a response status code of 200
    And the response should have a todo task with title: "title" and description: "description"

    Examples:
      | ID | title    | description                |
      | 1  | ToDo 1   | New description of ToDo 1  |
      | 2  | ToDo 2   | New description of ToDo 2  |

  # Alternate Flow
  Scenario Outline: Update a ToDo task's description successfully using POST
    When I send a POST request to "/todos/<ID>" using title: "title" and description: "description"
    Then I should receive a response status code of 200
    And the response should have a todo task with title: "title" and description: "description"

    Examples:
      | ID | title    | description                |
      | 1  | ToDo 1   | New description of ToDo 1  |
      | 2  | ToDo 2   | New description of ToDo 2  |

  # Error Flow
  Scenario Outline: Update a ToDo task's description with invalid ID using PUT
    When I send a PUT request to "/todos/-1" using title: "title" and description: "description"
    Then I should receive a response status code of 404
    And the response should contain the error message "Invalid GUID for -1 entity todo"

    Examples:
      | title    | description                |
      | ToDo 1   | New description of ToDo 1  |
      | ToDo 2   | New description of ToDo 2  |