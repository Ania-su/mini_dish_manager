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

    }

    private int id;
    private String name;
    private double price;
    private CategoryEnum category;
    private Dish dish;

    public String getDishName() {
        return dish.getName();
    }

}