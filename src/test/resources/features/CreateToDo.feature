Feature: Create New ToDo
  As a user, I want to create a new todo task so that I can have the task saved in the list of todos for future reference

  Background:
    Given the service is running

    # Normal Flow
    Scenario Outline: Create a ToDo task successfully using a title and description
      When I send a POST request to "todos" using title: "title" and description: "description"
      Then I should receive a response status code of 201
      And the response should have a todo task with title: "title" and description: "description"

      Examples:
      | title    | description            |
      | ToDo 1   | Description of ToDo 1  |
      | ToDo 2   | Description of ToDo 2  |

