package ru.moseichuck.repository;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.moseichuck.exception.RepositoryException;

public class DatabaseMigrator {

    private static final Logger log = LoggerFactory.getLogger(DatabaseMigrator.class);

    private final String url;

    private final String username;

    private final String password;

    private final String schema;

    public DatabaseMigrator(String url, String username, String password, String schema) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.schema = schema;
    }

    public void runMigrations() {
        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(url, username, password)
                    .schemas(schema)
                    .defaultSchema(schema)
                    .locations("classpath:db/migration")
                    .load();

            flyway.migrate();
            log.info("Миграции базы данных применены");
        } catch (Exception e) {
            log.error("Не удалось применить миграции базы данных", e);
            throw new RepositoryException("Не удалось применить миграции базы данных", e);
        }
    }
}
