package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class Dish {

    public enum DishTypeEnum {
        START,
        MAIN,
        DESSERT
    }

    private int id;
    private String name;
    private DishTypeEnum dishType;
    private List<Ingredient> ingredients = new ArrayList<>();

    public double getDishCost() {
        double cost = 0;
        for (Ingredient ingredient : ingredients) {
            Object quantity = ingredient.getRequiredQuantity();
            if (quantity == null) {
                throw new IllegalArgumentException("Required quantity is null");
            }
            else {
                cost += quantity * ingredient.getPrice();
            }
        }
        return cost;
    }

}
