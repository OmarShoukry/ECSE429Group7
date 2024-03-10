Feature: Get a ToDo
  As a user, I want to get a specific todo task so that I can retrieve its details

  Background:
    Given the service is running

  # Normal Flow
  Scenario Outline: Get a ToDo task using its ID
    When I send a GET request to "/todos/<ID>"
    Then I should receive a response status code of 200

    Examples:
      | ID |
      | 1  |
      | 2  |

  # Alternate Flow
  Scenario Outline:  Get a ToDo task using its ID as filter
    When I send a GET request to "/todos/<ID>" using filter "?id=<ID>"
    Then I should receive a response status code of 200

    Examples:
      | ID |
      | 1  |
      | 2  |


  # Error Flow
  Scenario: Get a ToDo task with invalid ID
    When I send a GET request to "todos/-1"
    Then I should receive a response status code of 404
    And the response should contain the error message "Could not find an instance with todos/-1"