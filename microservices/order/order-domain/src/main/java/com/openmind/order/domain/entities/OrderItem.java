package com.openmind.order.domain.entities;

import com.openmind.order.domain.valueobjects.Money;
import com.openmind.shared.domain.Entity;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderItem extends Entity {

    private UUID productId;
    private String productName;
    private int quantity;
    private Money unitPrice;

    // Required for MongoDB deserialization
    protected OrderItem() {
        super();
    }

    private OrderItem(UUID id, UUID productId, String productName, int quantity, Money unitPrice) {
        super(id);
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public static OrderItem create(UUID productId, String productName, int quantity, Money unitPrice) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        return new OrderItem(UUID.randomUUID(), productId, productName, quantity, unitPrice);
    }

    public Money getTotalPrice() {
        return Money.create(unitPrice.getAmount().multiply(BigDecimal.valueOf(quantity)));
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Money unitPrice) {
        this.unitPrice = unitPrice;
    }
}
