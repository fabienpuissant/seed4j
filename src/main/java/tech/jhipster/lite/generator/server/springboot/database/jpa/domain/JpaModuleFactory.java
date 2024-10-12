package tech.jhipster.lite.generator.server.springboot.database.jpa.domain;

import static tech.jhipster.lite.generator.server.springboot.database.jpa.domain.SQLCommonModuleBuilder.sqlCommonModuleBuilder;
import static tech.jhipster.lite.module.domain.JHipsterModule.artifactId;
import static tech.jhipster.lite.module.domain.JHipsterModule.documentationTitle;
import static tech.jhipster.lite.module.domain.JHipsterModule.from;
import static tech.jhipster.lite.module.domain.JHipsterModule.groupId;
import static tech.jhipster.lite.module.domain.JHipsterModule.javaDependency;
import static tech.jhipster.lite.module.domain.JHipsterModule.lineBeforeText;
import static tech.jhipster.lite.module.domain.JHipsterModule.path;
import static tech.jhipster.lite.module.domain.JHipsterModule.propertyKey;
import static tech.jhipster.lite.module.domain.JHipsterModule.propertyValue;
import static tech.jhipster.lite.module.domain.JHipsterModule.to;
import static tech.jhipster.lite.module.domain.JHipsterModule.toSrcTestJava;

import tech.jhipster.lite.module.domain.JHipsterModule;
import tech.jhipster.lite.module.domain.LogLevel;
import tech.jhipster.lite.module.domain.docker.DockerImageVersion;
import tech.jhipster.lite.module.domain.docker.DockerImages;
import tech.jhipster.lite.module.domain.file.JHipsterSource;
import tech.jhipster.lite.module.domain.javadependency.JavaDependency;
import tech.jhipster.lite.module.domain.javadependency.JavaDependencyScope;
import tech.jhipster.lite.module.domain.properties.JHipsterModuleProperties;

public class JpaModuleFactory {

  public static final String ORG_POSTGRESQL = "org.postgresql";
  private static final String MYSQL = "mysql";
  private static final String MYSQL_GROUP_ID = "com.mysql";
  private static final String MYSQL_ARTIFACT_ID = "mysql-connector-j";

  private static final String SPRING_DATASOURCE_URL = "spring.datasource.url";
  private static final String SPRING_DATASOURCE_USERNAME = "spring.datasource.username";
  private static final String SPRING_DATASOURCE_DRIVER_CLASS_NAME = "spring.datasource.driver-class-name";

  private final DockerImages dockerImages;

  public JpaModuleFactory(DockerImages dockerImages) {
    this.dockerImages = dockerImages;
  }

  public JHipsterModule buildPostgresql(JHipsterModuleProperties properties) {
    DockerImageVersion dockerImage = dockerImages.get("postgres");

    //@formatter:off
    return sqlCommonModuleBuilder(
      properties,
      DatabaseType.POSTGRESQL,
      dockerImage,
      documentationTitle("Postgresql"),
      artifactId("postgresql")
    )
      .javaDependencies()
        .addDependency(
          JavaDependency.builder()
            .groupId(groupId(ORG_POSTGRESQL))
            .artifactId(artifactId("postgresql"))
            .scope(JavaDependencyScope.RUNTIME)
            .build()
        )
        .and()
      .springMainProperties()
        .set(propertyKey(SPRING_DATASOURCE_URL), propertyValue("jdbc:postgresql://localhost:5432/" + properties.projectBaseName().name()))
        .set(propertyKey(SPRING_DATASOURCE_USERNAME), propertyValue(properties.projectBaseName().name()))
        .set(propertyKey(SPRING_DATASOURCE_DRIVER_CLASS_NAME), propertyValue("org.postgresql.Driver"))
        .and()
      .springTestProperties()
        .set(
          propertyKey(SPRING_DATASOURCE_URL),
          propertyValue(
            "jdbc:tc:postgresql:" + dockerImage.version().get() + ":///" + properties.projectBaseName().name() + "?TC_TMPFS=/testtmpfs:rw"
          )
        )
        .and()
      .springMainLogger(ORG_POSTGRESQL, LogLevel.WARN)
      .springTestLogger(ORG_POSTGRESQL, LogLevel.WARN)
      .springTestLogger("org.jboss.logging", LogLevel.WARN)
      .build();
    //@formatter:on
  }

  public JHipsterModule buildMariaDB(JHipsterModuleProperties properties) {
    //@formatter:off
    return sqlCommonModuleBuilder(
      properties,
      DatabaseType.MARIADB,
      dockerImages.get("mariadb"),
      documentationTitle("MariaDB"),
      artifactId("mariadb")
    )
      .javaDependencies()
        .addDependency(
          javaDependency().groupId("org.mariadb.jdbc").artifactId("mariadb-java-client").scope(JavaDependencyScope.RUNTIME).build()
        )
        .and()
      .springMainProperties()
        .set(propertyKey(SPRING_DATASOURCE_URL), propertyValue("jdbc:mariadb://localhost:3306/" + properties.projectBaseName().name()))
        .set(propertyKey(SPRING_DATASOURCE_USERNAME), propertyValue("root"))
        .set(propertyKey(SPRING_DATASOURCE_DRIVER_CLASS_NAME), propertyValue("org.mariadb.jdbc.Driver"))
        .and()
      .build();
    //@formatter:on
  }

  public JHipsterModule buildMySQL(JHipsterModuleProperties properties) {
    //@formatter:off
    return sqlCommonModuleBuilder(properties, DatabaseType.MYSQL, dockerImages.get(MYSQL), documentationTitle("MySQL"), artifactId(MYSQL))
      .javaDependencies()
        .addDependency(javaDependency().groupId(MYSQL_GROUP_ID).artifactId(MYSQL_ARTIFACT_ID).scope(JavaDependencyScope.RUNTIME).build())
        .and()
      .springMainProperties()
        .set(propertyKey(SPRING_DATASOURCE_URL), propertyValue("jdbc:mysql://localhost:3306/" + properties.projectBaseName().name()))
        .set(propertyKey(SPRING_DATASOURCE_USERNAME), propertyValue("root"))
        .set(propertyKey(SPRING_DATASOURCE_DRIVER_CLASS_NAME), propertyValue("com.mysql.cj.jdbc.Driver"))
        .and()
      .build();
    //@formatter:on
  }

  public JHipsterModule buildMsSQL(JHipsterModuleProperties properties) {
    DockerImageVersion dockerImage = dockerImages.get("mcr.microsoft.com/mssql/server");
    JHipsterSource source = from("server/springboot/database/common");

    //@formatter:off
    return sqlCommonModuleBuilder(properties, DatabaseType.MSSQL, dockerImage, documentationTitle("MsSQL"), artifactId("mssqlserver"))
      .files()
        .add(source.append("docker").template("container-license-acceptance.txt"), to("src/test/resources/container-license-acceptance.txt"))
        .add(
          source.template("MsSQLTestContainerExtension.java"),
          toSrcTestJava().append(properties.basePackage().path()).append("MsSQLTestContainerExtension.java")
        )
        .and()
      .javaDependencies()
        .addDependency(javaDependency().groupId("com.microsoft.sqlserver").artifactId("mssql-jdbc").scope(JavaDependencyScope.RUNTIME).build())
        .and()
      .springMainProperties()
        .set(
          propertyKey(SPRING_DATASOURCE_URL),
          propertyValue("jdbc:sqlserver://localhost:1433;database=" + properties.projectBaseName().name() + ";trustServerCertificate=true")
        )
        .set(propertyKey(SPRING_DATASOURCE_USERNAME), propertyValue("SA"))
        .set(propertyKey("spring.datasource.password"), propertyValue("yourStrong(!)Password"))
        .set(propertyKey(SPRING_DATASOURCE_DRIVER_CLASS_NAME), propertyValue("com.microsoft.sqlserver.jdbc.SQLServerDriver"))
        .set(propertyKey("spring.jpa.hibernate.ddl-auto"), propertyValue("update"))
        .set(propertyKey("spring.jpa.properties.hibernate.criteria.literal_handling_mode"), propertyValue("BIND"))
        .set(propertyKey("spring.jpa.properties.hibernate.dialect"), propertyValue("org.hibernate.dialect.SQLServer2012Dialect"))
        .set(propertyKey("spring.jpa.properties.hibernate.format_sql"), propertyValue(true))
        .set(propertyKey("spring.jpa.properties.hibernate.jdbc.fetch_size"), propertyValue(150))
        .and()
      .springTestProperties()
        .set(
          propertyKey(SPRING_DATASOURCE_URL),
          propertyValue(
            "jdbc:tc:sqlserver:///;database=" + properties.projectBaseName().name() + ";trustServerCertificate=true?TC_TMPFS=/testtmpfs:rw"
          )
        )
        .set(propertyKey(SPRING_DATASOURCE_USERNAME), propertyValue("SA"))
        .set(propertyKey("spring.datasource.password"), propertyValue("yourStrong(!)Password"))
        .and()
      .mandatoryReplacements()
        .in(path("src/test/java").append(properties.basePackage().path()).append("IntegrationTest.java"))
          .add(
            lineBeforeText("import org.springframework.boot.test.context.SpringBootTest;"),
            "import org.junit.jupiter.api.extension.ExtendWith;"
          )
          .add(lineBeforeText("public @interface"), "@ExtendWith(MsSQLTestContainerExtension.class)")
          .and()
        .and()
      .springMainLogger("com.microsoft.sqlserver", LogLevel.WARN)
      .springMainLogger("org.reflections", LogLevel.WARN)
      .build();
    //@formatter:on
  }
}
