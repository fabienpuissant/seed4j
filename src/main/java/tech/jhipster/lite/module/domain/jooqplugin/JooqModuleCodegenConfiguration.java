package tech.jhipster.lite.module.domain.jooqplugin;

import tech.jhipster.lite.shared.error.domain.Assert;

public final class JooqModuleCodegenConfiguration {

  private final DatabaseType database;
  private final String databaseUrl;
  private final String user;
  private final String inputSchema;
  private final String password;

  private JooqModuleCodegenConfiguration(JooqModuleCodegenConfigurationBuilder builder) {
    Assert.notNull("database", builder.database);
    Assert.notNull("databaseUrl", builder.databaseUrl);
    Assert.notNull("user", builder.user);
    Assert.notNull("inputSchema", builder.inputSchema);

    this.database = builder.database;
    this.databaseUrl = builder.databaseUrl;
    this.user = builder.user;
    this.inputSchema = builder.inputSchema;
    this.password = builder.password == null ? "" : builder.password;
  }

  public String getConfiguration() {
    return String.format(
      """
      <jdbc>
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
      databaseUrl,
      user,
      password,
      databaseJooqName(),
      inputSchema
    );
  }

  private String databaseJooqName() {
    return switch (database) {
      case POSTGRESQL -> "org.jooq.meta.postgres.PostgresDatabase";
      case MYSQL -> "org.jooq.meta.mysql.MySQLDatabase";
      case MARIADB -> "org.jooq.meta.mariadb.MariaDBDatabase";
      case MSSQL -> "org.jooq.meta.sqlserver.SQLServerDatabase";
    };
  }

  public static JooqModuleCodegenConfigurationBuilder builder() {
    return new JooqModuleCodegenConfigurationBuilder();
  }

  public interface JooqModuleCodegenConfigurationDatabaseBuilder {
    JooqModuleCodegenConfigurationDatabaseUrlBuilder database(DatabaseType database);
  }

  public interface JooqModuleCodegenConfigurationDatabaseUrlBuilder {
    JooqModuleCodegenConfigurationUserBuilder databaseUrl(String databaseUrl);
  }

  public interface JooqModuleCodegenConfigurationUserBuilder {
    JooqModuleCodegenConfigurationInputSchemaBuilder user(String user);
  }

  public interface JooqModuleCodegenConfigurationInputSchemaBuilder {
    JooqModuleCodegenConfigurationBuilder inputSchema(String inputSchema);
  }

  public interface JooqModuleCodegenConfigurationPasswordBuilder {
    JooqModuleCodegenConfiguration password(String password);
  }

  public static final class JooqModuleCodegenConfigurationBuilder
    implements
      JooqModuleCodegenConfigurationDatabaseBuilder,
      JooqModuleCodegenConfigurationDatabaseUrlBuilder,
      JooqModuleCodegenConfigurationUserBuilder,
      JooqModuleCodegenConfigurationInputSchemaBuilder,
      JooqModuleCodegenConfigurationPasswordBuilder {

    private DatabaseType database;
    private String databaseUrl;
    private String user;
    private String inputSchema;
    private String password;

    @Override
    public JooqModuleCodegenConfigurationDatabaseUrlBuilder database(DatabaseType database) {
      this.database = database;

      return this;
    }

    @Override
    public JooqModuleCodegenConfigurationUserBuilder databaseUrl(String databaseUrl) {
      this.databaseUrl = databaseUrl;

      return this;
    }

    @Override
    public JooqModuleCodegenConfigurationInputSchemaBuilder user(String user) {
      this.user = user;

      return this;
    }

    @Override
    public JooqModuleCodegenConfigurationBuilder inputSchema(String inputSchema) {
      this.inputSchema = inputSchema;

      return this;
    }

    @Override
    public JooqModuleCodegenConfiguration password(String password) {
      this.password = password;

      return build();
    }

    public JooqModuleCodegenConfiguration build() {
      return new JooqModuleCodegenConfiguration(this);
    }
  }
}
