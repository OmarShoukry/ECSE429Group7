Feature: Get all ToDo tasks
  As a user, I want to get all todo tasks so that I can retrieve their details

  Background:
    Given the service is running

  # Normal Flow
  Scenario: Get all ToDo tasks
    When I send a GET request to "/todos"
    Then I should receive a response status code of 200
    And the response should contain a list of todos

  # Alternate Flow
  Scenario:  Get all Todo tasks that are not yet completed
    When I send a GET request to "/todos" using filter "?doneStatus=false"
    Then I should receive a response status code of 200
    And the response should contain a list of todos

  # Error Flow
  Scenario: Get all ToDo tasks using an invalid endpoint
    When I send a GET request to "todos/all"
    Then I should receive a response status code of 404
    And the response should contain the error message "Could not find an instance with todos/all"