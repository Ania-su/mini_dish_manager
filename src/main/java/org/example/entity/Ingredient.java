package org.example.entity;

import lombok.*;

import java.time.Instant;
import java.util.List;

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
    private List<StockMovement> stockMovementList;

    public String getDishName() {
        return dish != null ? dish.getName() : null;
    }

    public Ingredient(int id, String name, Double price, CategoryEnum category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public StockValue getStockValueAt(Instant t) {
        double quantity = 0.0;
        DishIngredient.Unit unit = DishIngredient.Unit.KG;

        if (stockMovementList == null || stockMovementList.isEmpty()) {
            return new StockValue(0.0, unit);
        }

        for (StockMovement sm : stockMovementList) {

            if (sm.getCreationDateTime().isAfter(t)) {
                continue;
            }

            double q = sm.getValue().getQuantity();

            if (sm.getType() == StockMovement.MovementType.IN) {
                quantity += q;
            } else if (sm.getType() == StockMovement.MovementType.OUT) {
                quantity -= q;
            }
        }

        return new StockValue(quantity, unit);

    }
}