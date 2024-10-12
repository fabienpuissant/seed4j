package tech.jhipster.lite.generator.server.springboot.database.jpa.domain;

import static tech.jhipster.lite.module.domain.JHipsterModule.JHipsterModuleBuilder;
import static tech.jhipster.lite.module.domain.JHipsterModule.artifactId;
import static tech.jhipster.lite.module.domain.JHipsterModule.from;
import static tech.jhipster.lite.module.domain.JHipsterModule.groupId;
import static tech.jhipster.lite.module.domain.JHipsterModule.javaDependency;
import static tech.jhipster.lite.module.domain.JHipsterModule.moduleBuilder;
import static tech.jhipster.lite.module.domain.JHipsterModule.propertyKey;
import static tech.jhipster.lite.module.domain.JHipsterModule.propertyValue;
import static tech.jhipster.lite.module.domain.JHipsterModule.toSrcMainDocker;
import static tech.jhipster.lite.module.domain.JHipsterModule.toSrcMainJava;

import tech.jhipster.lite.module.domain.DocumentationTitle;
import tech.jhipster.lite.module.domain.LogLevel;
import tech.jhipster.lite.module.domain.docker.DockerImageVersion;
import tech.jhipster.lite.module.domain.file.JHipsterDestination;
import tech.jhipster.lite.module.domain.file.JHipsterSource;
import tech.jhipster.lite.module.domain.javabuild.ArtifactId;
import tech.jhipster.lite.module.domain.javadependency.JavaDependency;
import tech.jhipster.lite.module.domain.javadependency.JavaDependencyScope;
import tech.jhipster.lite.module.domain.javaproperties.PropertyValue;
import tech.jhipster.lite.module.domain.properties.JHipsterModuleProperties;
import tech.jhipster.lite.shared.error.domain.Assert;

final class SQLCommonModuleBuilder {

  private static final String ORG_HIBERNATE = "org.hibernate.orm";
  private static final PropertyValue FALSE = propertyValue(false);
  private static final PropertyValue TRUE = propertyValue(true);

  private SQLCommonModuleBuilder() {}

  public static JHipsterModuleBuilder sqlCommonModuleBuilder(
    JHipsterModuleProperties properties,
    DatabaseType databaseType,
    DockerImageVersion dockerImage,
    DocumentationTitle documentationTitle,
    ArtifactId testContainerArtifactId
  ) {
    Assert.notNull("properties", properties);
    Assert.notNull("databaseType", databaseType);
    Assert.notNull("dockerImage", dockerImage);
    Assert.notNull("documentationTitle", documentationTitle);
    Assert.notNull("testContainerArtifactId", testContainerArtifactId);

    String databaseId = databaseType.id();
    JHipsterSource commonSource = from("server/springboot/database/common");
    JHipsterSource jpaSource = from("server/springboot/database/jpa");
    JHipsterDestination mainDestination = toSrcMainJava()
      .append(properties.packagePath())
      .append("wire")
      .append(databaseId)
      .append("infrastructure/secondary/");

    //@formatter:off
    return moduleBuilder(properties)
      .context()
        .put("srcMainDocker", "src/main/docker") // To be used in <databaseId>>.md file
        .put("databaseType", databaseId)
        .put(databaseId + "DockerImageWithVersion", dockerImage.fullName()) // To be used in <databaseId>.yml docker-compose file
        .and()
      .documentation(documentationTitle, commonSource.template("databaseType.md"))
      .startupCommands()
        .dockerCompose(startupCommand(databaseId))
        .and()
      .files()
        .add(jpaSource.template("DatabaseConfiguration.java"), mainDestination.append("DatabaseConfiguration.java"))
        .add(commonSource.append("docker").template(databaseId + ".yml"), toSrcMainDocker().append(databaseId + ".yml"))
        .and()
      .javaDependencies()
        .addDependency(groupId("org.springframework.boot"), artifactId("spring-boot-starter-data-jpa"))
        .addDependency(groupId("com.zaxxer"), artifactId("HikariCP"))
        .addDependency(groupId(ORG_HIBERNATE), artifactId("hibernate-core"))
        .addDependency(testContainer(testContainerArtifactId))
        .and()
      .springMainProperties()
        .set(propertyKey("spring.datasource.password"), propertyValue(""))
        .set(propertyKey("spring.datasource.type"), propertyValue("com.zaxxer.hikari.HikariDataSource"))
        .set(propertyKey("spring.datasource.hikari.poolName"), propertyValue("Hikari"))
        .set(propertyKey("spring.datasource.hikari.auto-commit"), FALSE)
        .set(propertyKey("spring.data.jpa.repositories.bootstrap-mode"), propertyValue("deferred"))
        .set(propertyKey("spring.jpa.hibernate.ddl-auto"), propertyValue("none"))
        .set(
          propertyKey("spring.jpa.hibernate.naming.implicit-strategy"),
          propertyValue("org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy")
        )
        .set(
          propertyKey("spring.jpa.hibernate.naming.physical-strategy"),
          propertyValue("org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy")
        )
        .set(propertyKey("spring.jpa.open-in-view"), FALSE)
        .set(propertyKey("spring.jpa.properties.hibernate.connection.provider_disables_autocommit"), TRUE)
        .set(propertyKey("spring.jpa.properties.hibernate.generate_statistics"), FALSE)
        .set(propertyKey("spring.jpa.properties.hibernate.jdbc.batch_size"), propertyValue(25))
        .set(propertyKey("spring.jpa.properties.hibernate.jdbc.time_zone"), propertyValue("UTC"))
        .set(propertyKey("spring.jpa.properties.hibernate.order_inserts"), TRUE)
        .set(propertyKey("spring.jpa.properties.hibernate.order_updates"), TRUE)
        .set(propertyKey("spring.jpa.properties.hibernate.query.fail_on_pagination_over_collection_fetch"), TRUE)
        .set(propertyKey("spring.jpa.properties.hibernate.query.in_clause_parameter_padding"), TRUE)
        .and()
      .springTestProperties()
        .set(
          propertyKey("spring.datasource.url"),
          propertyValue("jdbc:tc:" + dockerImage.fullName() + ":///" + properties.projectBaseName().name())
        )
        .set(propertyKey("spring.datasource.username"), propertyValue(properties.projectBaseName().name()))
        .set(propertyKey("spring.datasource.password"), propertyValue(""))
        .set(propertyKey("spring.datasource.driver-class-name"), propertyValue("org.testcontainers.jdbc.ContainerDatabaseDriver"))
        .set(propertyKey("spring.datasource.hikari.maximum-pool-size"), propertyValue(2))
        .and()
      .springMainLogger("org.hibernate.validator", LogLevel.WARN)
      .springMainLogger(ORG_HIBERNATE, LogLevel.WARN)
      .springMainLogger("org.hibernate.ejb.HibernatePersistence", LogLevel.OFF)
      .springTestLogger("org.hibernate.validator", LogLevel.WARN)
      .springTestLogger(ORG_HIBERNATE, LogLevel.WARN)
      .springTestLogger("org.hibernate.ejb.HibernatePersistence", LogLevel.OFF)
      .springTestLogger("com.github.dockerjava", LogLevel.WARN)
      .springTestLogger("org.testcontainers", LogLevel.WARN);
    //@formatter:on
  }

  private static JavaDependency testContainer(ArtifactId testContainerArtifactId) {
    return javaDependency()
      .groupId("org.testcontainers")
      .artifactId(testContainerArtifactId)
      .dependencySlug("%s-%s".formatted("testcontainers", testContainerArtifactId))
      .versionSlug("testcontainers")
      .scope(JavaDependencyScope.TEST)
      .build();
  }

  private static String startupCommand(String databaseId) {
    return "src/main/docker/" + databaseId + ".yml";
  }
}
