package com.example.ecommerce.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public class OrderCreationDto {

    @NotNull
    private List<OrderItemCreationDto> items;

    // Getters and Setters
    public List<OrderItemCreationDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemCreationDto> items) {
        this.items = items;
    }
}