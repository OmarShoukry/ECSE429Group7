Feature: Create New Project
  As a user, I want to create a new project so that I can manage a new set of tasks.

  Background:
    Given the service is running

    # Normal Flow
    Scenario Outline: Create a new project successfully with a title and description
      When I send a POST request to "projects" with title "title" and description "description"
      Then I should receive a response status code of 201
      And the response should contain a project with name "title" and description "description"

    Examples:
      | title        | description               |
      | Project 1    | Description of Project 1  |
      | Project 2    | Description of Project 2  |
