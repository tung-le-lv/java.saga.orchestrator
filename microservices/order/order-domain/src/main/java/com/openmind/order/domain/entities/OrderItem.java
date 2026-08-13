package com.openmind.order.domain.entities;

import com.openmind.order.domain.valueobjects.Money;
import com.openmind.shared.domain.Entity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@jakarta.persistence.Entity
@Table(name = "order_items")
public class OrderItem extends Entity {

    @Column(nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private int quantity;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "unit_price"))
    private Money unitPrice;

    // Required by JPA
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
        return Money.create(unitPrice.amount().multiply(BigDecimal.valueOf(quantity)));
    }

    public UUID getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }
}
