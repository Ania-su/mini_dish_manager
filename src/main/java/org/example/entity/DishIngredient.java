package org.example.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

public class DishIngredient {

    public enum Unit {
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
