package com.openmind.fulfillment.domain.entities;

import com.openmind.shared.domain.Entity;

import java.util.UUID;

public class FulfillmentItem extends Entity {

    private UUID productId;
    private String productName;
    private int quantity;

    // Required for MongoDB deserialization
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
}
