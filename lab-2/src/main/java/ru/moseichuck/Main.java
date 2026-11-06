package ru.moseichuck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.moseichuck.dto.OrderEntity;
import ru.moseichuck.repository.ConnectionFactory;
import ru.moseichuck.repository.DatabaseMigrator;
import ru.moseichuck.repository.JdbcOrderRepository;
import ru.moseichuck.repository.OrderRepository;
import ru.moseichuck.repository.PropertiesService;

import java.util.List;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static final String ORDER_TABLE = "orders";

    public static void main(String[] args) {
        PropertiesService properties = new PropertiesService();
        String url = properties.get("database.url");
        String username = properties.get("database.username");
        String password = properties.get("database.password");
        String schema = properties.get("database.schema");

        new DatabaseMigrator(url, username, password, schema).runMigrations();

        OrderRepository repository = new JdbcOrderRepository(
                new ConnectionFactory(url, username, password, schema), schema, ORDER_TABLE);

        demonstrateCrud(repository);
    }

    private static void demonstrateCrud(OrderRepository repository) {
        int firstOrderId = repository.save(new OrderEntity("Иванов Иван", 199.99));
        int secondOrderId = repository.save(new OrderEntity("Петров Петр", 49.50));
        log.info("Созданы заказы: id={}, id={}", firstOrderId, secondOrderId);

        log.info("findById({}): {}", firstOrderId, repository.findById(firstOrderId));
        log.info("findByCustomerName(Петров Петр): {}", repository.findByCustomerName("Петров Петр"));

        OrderEntity orderToUpdate = repository.findById(firstOrderId);
        orderToUpdate.setAmount(299.99);
        log.info("update вернул: {}", repository.update(orderToUpdate));
        log.info("Заказ после обновления: {}", repository.findById(firstOrderId));

        repository.deleteById(secondOrderId);
        List<OrderEntity> remainingOrders = repository.findAll();
        log.info("findAll после удаления ({} шт.): {}", remainingOrders.size(), remainingOrders);
    }
}
