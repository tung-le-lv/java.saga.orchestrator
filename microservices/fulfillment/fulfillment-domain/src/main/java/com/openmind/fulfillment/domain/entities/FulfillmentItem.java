package com.openmind.fulfillment.domain.entities;

import com.openmind.shared.domain.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

import java.util.UUID;

@jakarta.persistence.Entity
@Table(name = "fulfillment_items")
public class FulfillmentItem extends Entity {

    @Column(nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private int quantity;

    // Required by JPA
    protected FulfillmentItem() {
        super();
    }

    private FulfillmentItem(UUID id, UUID productId, String productName, int quantity) {
        super(id);
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
    }

    public static FulfillmentItem create(UUID productId, String productName, int quantity) {
        return new FulfillmentItem(UUID.randomUUID(), productId, productName, quantity);
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
}
