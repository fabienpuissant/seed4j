Feature: Jooq MsSql module

  Scenario: Should add MsSQL elements using legacy url
    When I apply modules to default project
      | maven-java  |
      | spring-boot |
      | jooq-mssql  |
    Then I should have files in ""
      | pom.xml |
    And I should have files in "documentation"
      | mssql.md |
    And I should have files in "src/main/docker"
      | mssql.yml |
    And I should have files in "src/test/java/tech/jhipster/chips"
      | MsSQLTestContainerExtension.java |
    And I should have files in "src/test/resources"
      | container-license-acceptance.txt |
    And I should have files in "src/main/resources/config"
      | application.yml |
    And I should have files in "src/test/resources/config"
      | application-test.yml |

  Scenario: Should get MsSQL module properties definition
    When I get module "jooq-mssql" properties definition
    Then I should have properties definitions
      | Key                       | Type    | Mandatory |
      | packageName               | STRING  | true      |
      | baseName                  | STRING  | true      |
      | indentSize                | INTEGER | false     |
      | springConfigurationFormat | STRING  | false     |
