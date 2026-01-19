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
    private Double price;
    private CategoryEnum category;
    private Dish dish;

    public String getDishName() {
        return dish != null ? dish.getName() : null;
    }

    public Ingredient(int id, String name, Double price, CategoryEnum category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }
}