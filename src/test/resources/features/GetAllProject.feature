Feature: Get All Projects
  As a user, I want to get all projects so that I can view a list of all my projects

  Background:
    Given the service is running

  # Normal Flow
  Scenario: Get all projects successfully
    When I send a POST request to "projects" with title "Project 1" and description "description"
    And I send a GET request to "/projects"
    Then I should receive a response status code of 200
    And I should receive a list of projects with "Project 1"

  # Alternative Flow
  Scenario: Get all projects without having any created
    When I send a DELETE request to "/projects/1"
    And I send a GET request to "/projects"
    Then I should receive a response status code of 200
    And I should receive an empty list of projects

  # Error Flow
  Scenario: Get all projects from invalid endpoint
    When I send a GET request to "projects/-1"
    Then I should receive a response status code of 404
    And the response should contain the error message "Could not find an instance with projects/-1"