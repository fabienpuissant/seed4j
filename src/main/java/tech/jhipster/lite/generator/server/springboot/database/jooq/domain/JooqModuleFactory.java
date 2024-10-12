package tech.jhipster.lite.generator.server.springboot.database.jooq.domain;

import static tech.jhipster.lite.generator.server.springboot.database.jooq.domain.CommonModuleBuilder.commonModuleBuilder;
import static tech.jhipster.lite.module.domain.JHipsterModule.*;
import static tech.jhipster.lite.module.domain.JHipsterModule.lineBeforeText;

import tech.jhipster.lite.module.domain.JHipsterModule;
import tech.jhipster.lite.module.domain.LogLevel;
import tech.jhipster.lite.module.domain.docker.DockerImageVersion;
import tech.jhipster.lite.module.domain.docker.DockerImages;
import tech.jhipster.lite.module.domain.file.JHipsterSource;
import tech.jhipster.lite.module.domain.javadependency.JavaDependency;
import tech.jhipster.lite.module.domain.javadependency.JavaDependencyScope;
import tech.jhipster.lite.module.domain.mavenplugin.MavenBuildPhase;
import tech.jhipster.lite.module.domain.properties.JHipsterModuleProperties;

public class JooqModuleFactory {

  public static final String ORG_POSTGRESQL = "org.postgresql";
  private static final String MYSQL = "mysql";
  private static final String MYSQL_GROUP_ID = "com.mysql";
  private static final String MYSQL_ARTIFACT_ID = "mysql-connector-j";

  private static final String SPRING_DATASOURCE_URL = "spring.datasource.url";
  private static final String SPRING_DATASOURCE_USERNAME = "spring.datasource.username";
  private static final String SPRING_DATASOURCE_DRIVER_CLASS_NAME = "spring.datasource.driver-class-name";

  private final DockerImages dockerImages;

  public JooqModuleFactory(DockerImages dockerImages) {
    this.dockerImages = dockerImages;
  }

  public JHipsterModule buildPostgresql(JHipsterModuleProperties properties) {
    DockerImageVersion dockerImage = dockerImages.get("postgres");

    //@formatter:off
    return commonModuleBuilder(
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
      .mavenPlugins()
        .plugin(mavenPlugin()
          .groupId("org.jooq")
          .artifactId("jooq-codegen-maven")
          .versionSlug("jooq")
          .addExecution(pluginExecution().goals("generate").id("jooq-codegen").phase(MavenBuildPhase.GENERATE_RESOURCES))
          .configuration(jooqCodegenPluginConfiguration("jdbc:postgresql://localhost:5432/" + properties.projectBaseName().name(), DatabaseType.POSTGRESQL, properties.projectBaseName().name(), "", "public"))
          .build())
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
    return commonModuleBuilder(
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
      .mavenPlugins()
      .plugin(mavenPlugin()
        .groupId("org.jooq")
        .artifactId("jooq-codegen-maven")
        .versionSlug("jooq")
        .addExecution(pluginExecution().goals("generate").id("jooq-codegen").phase(MavenBuildPhase.GENERATE_RESOURCES))
        .configuration(jooqCodegenPluginConfiguration("jdbc:mariadb://localhost:3306/" + properties.projectBaseName().name(), DatabaseType.MARIADB, "root", "", properties.projectBaseName().name()))
        .build())
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
    return commonModuleBuilder(
      properties,
      DatabaseType.MYSQL,
      dockerImages.get(MYSQL),
      documentationTitle("MySQL"),
      artifactId(MYSQL)
    )
      .javaDependencies()
      .addDependency(javaDependency().groupId(MYSQL_GROUP_ID).artifactId(MYSQL_ARTIFACT_ID).scope(JavaDependencyScope.RUNTIME).build())
      .and()
      .mavenPlugins()
      .plugin(mavenPlugin()
        .groupId("org.jooq")
        .artifactId("jooq-codegen-maven")
        .versionSlug("jooq")
        .addExecution(pluginExecution().goals("generate").id("jooq-codegen").phase(MavenBuildPhase.GENERATE_RESOURCES))
        .configuration(jooqCodegenPluginConfiguration("jdbc:mysql://localhost:3306/" + properties.projectBaseName().name(), DatabaseType.MYSQL, "root", "", properties.projectBaseName().name()))
        .build())
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
    return commonModuleBuilder(
      properties,
      DatabaseType.MSSQL,
      dockerImage,
      documentationTitle("MsSQL"),
      artifactId("mssqlserver")
    )
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
      .mavenPlugins()
      .plugin(mavenPlugin()
        .groupId("org.jooq")
        .artifactId("jooq-codegen-maven")
        .versionSlug("jooq")
        .addExecution(pluginExecution().goals("generate").id("jooq-codegen").phase(MavenBuildPhase.GENERATE_RESOURCES))
        .configuration(jooqCodegenPluginConfiguration("jdbc:sqlserver://localhost:1433;database=" + properties.projectBaseName().name() + ";trustServerCertificate=true", DatabaseType.MSSQL, "SA", "yourStrong(!)Password", "model"))
        .build())
      .and()
      .springMainProperties()
      .set(
        propertyKey(SPRING_DATASOURCE_URL),
        propertyValue("jdbc:sqlserver://localhost:1433;database=" + properties.projectBaseName().name() + ";trustServerCertificate=true")
      )
      .set(propertyKey(SPRING_DATASOURCE_USERNAME), propertyValue("SA"))
      .set(propertyKey("spring.datasource.password"), propertyValue("yourStrong(!)Password"))
      .set(propertyKey(SPRING_DATASOURCE_DRIVER_CLASS_NAME), propertyValue("com.microsoft.sqlserver.jdbc.SQLServerDriver"))
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

  //TODO Create an API for MavenPluginConfiguration with a builder
  private String jooqCodegenPluginConfiguration(
    String databaseUrl,
    DatabaseType databaseType,
    String user,
    String password,
    String inputSchema
  ) {
    return String.format(
      """
      <jdbc>
        <driver>%s</driver>
        <url>%s</url>
        <user>%s</user>
        <password>%s</password>
      </jdbc>
      <generator>
        <database>
          <name>%s</name>
          <includes>.*</includes>
          <inputSchema>%s</inputSchema>
        </database>
        <target>
          <packageName>org.jooq.codegen</packageName>
          <directory>target/generated-sources/jooq</directory>
        </target>
      </generator>
      """,
      databaseDriver(databaseType),
      databaseUrl,
      user,
      password,
      databaseJooqName(databaseType),
      inputSchema
    );
  }

  private static String databaseJooqName(DatabaseType databaseType) {
    return switch (databaseType) {
      case POSTGRESQL -> "org.jooq.meta.postgres.PostgresDatabase";
      case MYSQL -> "org.jooq.meta.mysql.MySQLDatabase";
      case MARIADB -> "org.jooq.meta.mariadb.MariaDBDatabase";
      case MSSQL -> "org.jooq.meta.sqlserver.SQLServerDatabase";
    };
  }

  private static String databaseDriver(DatabaseType databaseType) {
    return switch (databaseType) {
      case POSTGRESQL -> "org.postgresql.Driver";
      case MARIADB -> "org.mariadb.jdbc.Driver";
      case MYSQL -> "com.mysql.jdbc.Driver";
      case MSSQL -> "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    };
  }
}
