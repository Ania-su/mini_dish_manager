package org.example.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

public class Ingredient {

    public enum CategoryEnum {
        VEGETABLE,
        ANIMAL,
        MARINE,
        DIARY,
        OTHER
    }

    private int id;
    private String name;
    private double price;
    private CategoryEnum category;
    private Dish dish;
    private Double requiredQuantity;

    public String getDishName() {
        return dish != null ? dish.getName() : null;
    }

    public Ingredient(int id, String name, double price, CategoryEnum category, Double requiredQuantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.requiredQuantity = requiredQuantity;
    }
}