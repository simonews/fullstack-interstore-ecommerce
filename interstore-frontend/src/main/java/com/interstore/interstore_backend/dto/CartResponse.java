package com.interstore.interstore_backend.dto;

import java.util.ArrayList;
import java.util.List;

public class CartResponse {
    private Long id;
    private Long userId;
    private List<CartItemResponse> items =  new ArrayList<>();

    // getter e setter
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }

    public void setUserId(Long userId) { this.userId = userId; }

    public List<CartItemResponse> getItems() { return items; }

    public void setItems(List<CartItemResponse> items) { this.items = items; }
}
