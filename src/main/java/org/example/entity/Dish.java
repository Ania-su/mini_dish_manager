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
    private Double sellingPrice;
    private List<DishIngredient> ingredients = new ArrayList<>();

    public double getDishCost() {
        double cost = 0;
        for (DishIngredient di : ingredients) {
            Double quantity = di.getQuantity_required();
            if (quantity == null) {
                throw new IllegalArgumentException("Exception : unknown value for required quantity");
            }
            else {
                cost += quantity * di.getIngredient().getPrice();
            }
        }
        return cost;
    }

    public double getGrossMargin() {
        if (sellingPrice == null) {
            throw new IllegalArgumentException("Exception : sellingPrice is null");
        }
        return sellingPrice - getDishCost();
    }

}
