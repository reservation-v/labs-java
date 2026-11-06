package ru.moseichuck.dto;

public class OrderEntity {

    private Integer id;

    private String customerName;

    private double amount;

    public OrderEntity(String customerName, double amount) {
        this.customerName = customerName;
        this.amount = amount;
    }

    public OrderEntity(Integer id, String customerName, double amount) {
        this.id = id;
        this.customerName = customerName;
        this.amount = amount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "OrderEntity{id=%s, customerName='%s', amount=%s}".formatted(id, customerName, amount);
    }
}
