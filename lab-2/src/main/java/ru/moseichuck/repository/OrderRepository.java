package ru.moseichuck.repository;

import ru.moseichuck.dto.OrderEntity;

import java.util.List;

public interface OrderRepository {

    // Create, возвращает id созданной записи
    int save(OrderEntity order);

    // Read, возвращает null, если запись не найдена
    OrderEntity findById(int id);

    // Read по второму полю сущности из варианта
    OrderEntity findByCustomerName(String customerName);

    List<OrderEntity> findAll();

    // Update по id, возвращает false, если запись не найдена
    boolean update(OrderEntity order);

    void deleteById(int id);
}
