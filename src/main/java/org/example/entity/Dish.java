package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private List<Ingredient> ingredients;

    public double getDishPrice() {
        return ingredients.stream()
                .mapToDouble(Ingredient::getPrice)
                .sum();
    }

}
