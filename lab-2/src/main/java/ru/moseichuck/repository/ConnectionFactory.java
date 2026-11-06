package ru.moseichuck.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionFactory {

    private final HikariDataSource dataSource;

    public ConnectionFactory(String url, String username, String password, String schema) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");
        config.setSchema(schema);
        config.setMaximumPoolSize(10);

        dataSource = new HikariDataSource(config);

        // закрываем пул при завершении приложения, чтобы не оставлять соединения в базе
        Runtime.getRuntime().addShutdownHook(new Thread(dataSource::close));
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
