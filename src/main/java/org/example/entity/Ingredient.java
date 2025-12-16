package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class Ingredient {

    enum CategoryEnum {
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

    public String getDishName() {
        return dish != null ? dish.getName() : null;
    }

}