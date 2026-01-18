package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString

public class DishIngredient {

    enum Unit {
        PCS,
        KG,
        L
    }

    private int id;
    private Dish dish;
    private Ingredient ingredient;
    private Double quantity_required;
    private Unit unit_type;

}
