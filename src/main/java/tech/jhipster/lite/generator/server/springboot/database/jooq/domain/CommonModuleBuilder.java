package tech.jhipster.lite.generator.server.springboot.database.jooq.domain;

import static tech.jhipster.lite.module.domain.JHipsterModule.*;

import tech.jhipster.lite.module.domain.DocumentationTitle;
import tech.jhipster.lite.module.domain.JHipsterModule;
import tech.jhipster.lite.module.domain.docker.DockerImageVersion;
import tech.jhipster.lite.module.domain.file.JHipsterSource;
import tech.jhipster.lite.module.domain.javabuild.ArtifactId;
import tech.jhipster.lite.module.domain.javadependency.JavaDependency;
import tech.jhipster.lite.module.domain.javadependency.JavaDependencyScope;
import tech.jhipster.lite.module.domain.javaproperties.PropertyValue;
import tech.jhipster.lite.module.domain.properties.JHipsterModuleProperties;
import tech.jhipster.lite.shared.error.domain.Assert;

final class CommonModuleBuilder {

  private static final PropertyValue FALSE = propertyValue(false);

  private CommonModuleBuilder() {}

  public static JHipsterModule.JHipsterModuleBuilder commonModuleBuilder(
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
    JHipsterSource source = from("server/springboot/database/common");

    //@formatter:off
    return moduleBuilder(properties)
      .context()
      .put("srcMainDocker", "src/main/docker") // To be used in <databaseId>>.md file
      .put("databaseType", databaseId)
      .put(databaseId + "DockerImageWithVersion", dockerImage.fullName()) // To be used in <databaseId>.yml docker-compose file
      .and()
      .documentation(documentationTitle, source.template("databaseType.md"))
      .startupCommands()
      .dockerCompose(startupCommand(databaseId))
      .and()
      .files()
      .add(source.append("docker").template(databaseId + ".yml"), toSrcMainDocker().append(databaseId + ".yml"))
      .and()
      .javaDependencies()
      .addDependency(groupId("org.springframework.boot"), artifactId("spring-boot-starter-jooq"))
      .addDependency(groupId("com.zaxxer"), artifactId("HikariCP"))
      .addDependency(testContainer(testContainerArtifactId))
      .and()
      .springMainProperties()
      .set(propertyKey("spring.datasource.password"), propertyValue(""))
      .set(propertyKey("spring.datasource.type"), propertyValue("com.zaxxer.hikari.HikariDataSource"))
      .set(propertyKey("spring.datasource.hikari.poolName"), propertyValue("Hikari"))
      .set(propertyKey("spring.datasource.hikari.auto-commit"), FALSE)
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
      .and();
    //@formatter:on
  }

  private static String startupCommand(String databaseId) {
    return "src/main/docker/" + databaseId + ".yml";
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
}
