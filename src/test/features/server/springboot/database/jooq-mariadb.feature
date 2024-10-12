Feature: Jooq MariaDB module

  Scenario: Should add MariaDB elements using legacy url
    When I apply modules to default project
      | maven-java   |
      | spring-boot  |
      | jooq-mariadb |
    Then I should have files in ""
      | pom.xml |
    And I should have files in "documentation"
      | mariadb.md |
    And I should have files in "src/main/docker"
      | mariadb.yml |
    And I should have files in "src/main/resources/config"
      | application.yml |
    And I should have files in "src/test/resources/config"
      | application-test.yml |

  Scenario: Should get MariaDB module properties definition
    When I get module "jooq-mariadb" properties definition
    Then I should have properties definitions
      | Key                       | Type    | Mandatory |
      | packageName               | STRING  | true      |
      | baseName                  | STRING  | true      |
      | indentSize                | INTEGER | false     |
      | springConfigurationFormat | STRING  | false     |

  Scenario: Should add MariaDB elements using module url
    When I apply "jooq-mariadb" module to default project with maven file
      | packageName | tech.jhipster.chips |
      | baseName    | jhipster            |
    Then I should have files in ""
      | pom.xml |
    And I should have files in "documentation"
      | mariadb.md |
    And I should have files in "src/main/docker"
      | mariadb.yml |
    And I should have files in "src/main/resources/config"
      | application.yml |
    And I should have files in "src/test/resources/config"
      | application-test.yml |
