package ru.moseichuck.repository;

import ru.moseichuck.dto.OrderEntity;
import ru.moseichuck.exception.RepositoryException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JdbcOrderRepository implements OrderRepository {

    private static final String COLUMNS = "id, customer_name, amount";

    private final ConnectionFactory connectionFactory;

    private final String table;

    public JdbcOrderRepository(ConnectionFactory connectionFactory, String schema, String table) {
        this.connectionFactory = connectionFactory;
        this.table = '"' + schema + '"' + '.' + '"' + table + '"';
    }

    @Override
    public int save(OrderEntity order) {
        String sql = "INSERT INTO " + table + " (customer_name, amount) VALUES (?, ?)";
        try (
                Connection connection = connectionFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, order.getCustomerName());
            statement.setDouble(2, order.getAmount());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    throw new RepositoryException("База данных не вернула идентификатор созданного заказа");
                }
                return generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            throw new RepositoryException("Не удалось сохранить заказ", e);
        }
    }

    @Override
    public OrderEntity findById(int id) {
        String sql = "SELECT " + COLUMNS + " FROM " + table + " WHERE id = ?";
        try (
                Connection connection = connectionFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            return findSingle(statement);
        } catch (SQLException e) {
            throw new RepositoryException("Не удалось найти заказ по id " + id, e);
        }
    }

    @Override
    public OrderEntity findByCustomerName(String customerName) {
        String sql = "SELECT " + COLUMNS + " FROM " + table + " WHERE customer_name = ? ORDER BY id LIMIT 1";
        try (
                Connection connection = connectionFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, customerName);
            return findSingle(statement);
        } catch (SQLException e) {
            throw new RepositoryException("Не удалось найти заказ покупателя " + customerName, e);
        }
    }

    @Override
    public List<OrderEntity> findAll() {
        String sql = "SELECT " + COLUMNS + " FROM " + table + " ORDER BY id";
        try (
                Connection connection = connectionFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            List<OrderEntity> orders = new ArrayList<>();
            while (resultSet.next()) {
                orders.add(map(resultSet));
            }
            return orders;
        } catch (SQLException e) {
            throw new RepositoryException("Не удалось получить список заказов", e);
        }
    }

    @Override
    public boolean update(OrderEntity order) {
        if (order.getId() == null) {
            throw new IllegalArgumentException("Для обновления заказа требуется идентификатор");
        }
        String sql = "UPDATE " + table + " SET customer_name = ?, amount = ? WHERE id = ?";
        try (
                Connection connection = connectionFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, order.getCustomerName());
            statement.setDouble(2, order.getAmount());
            statement.setInt(3, order.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Не удалось обновить заказ с id " + order.getId(), e);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM " + table + " WHERE id = ?";
        try (
                Connection connection = connectionFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Не удалось удалить заказ с id " + id, e);
        }
    }

    private OrderEntity findSingle(PreparedStatement statement) throws SQLException {
        try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return map(resultSet);
            }
            return null;
        }
    }

    private OrderEntity map(ResultSet resultSet) throws SQLException {
        return new OrderEntity(
                resultSet.getInt("id"),
                resultSet.getString("customer_name"),
                resultSet.getDouble("amount")
        );
    }
}
